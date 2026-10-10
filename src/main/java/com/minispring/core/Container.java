package com.minispring.core;

import com.minispring.annotation.*;
import com.minispring.context.ApplicationContext;
import com.minispring.lifecycle.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The runtime engine of the mini Spring container.
 *
 * Container owns the runtime responsibilities that should not be mixed into
 * discovery or ApplicationContext orchestration:
 *
 *   1. Store BeanDefinitions.
 *   2. Select a bean candidate for a requested type.
 *   3. Create beans through constructors or @Bean factory methods.
 *   4. Resolve constructor/factory-method dependencies.
 *   5. Apply scopes and singleton caching.
 *   6. Run the bean lifecycle and post-processors.
 *   7. Detect recursive resolution and destroy managed singletons.
 */
public class Container implements BeanResolver {

    // Metadata: "What beans are available and how should they behave?"
    private final Map<Class<?>, BeanDefinition> definitions =
            new HashMap<>();

    // Runtime singleton cache: "Which singleton instances already exist?"
    private final Map<Class<?>, Object> instances =
            new HashMap<>();

    // Active resolution path used to detect A -> B -> A style cycles.
    private final Deque<Class<?>> resolutionStack =
            new ArrayDeque<>();

    // Processors executed around bean initialization.
    private final List<BeanPostProcessor> postProcessors =
            new ArrayList<>();

    // Creation order is retained so destruction can happen in reverse order.
    private final List<Object> singletonCreationOrder =
            new ArrayList<>();

    // Set by ApplicationContext so context-aware beans receive their owner.
    private ApplicationContext applicationContext;

    /**
     * The container also acts as the default BeanResolver infrastructure bean.
     * Registering the already-existing container lets beans depend on the
     * BeanResolver interface without introducing a dependency on ApplicationContext.
     */
    public Container() {
        BeanDefinition selfDefinition = new BeanDefinition(
                Container.class,
                Scope.SINGLETON,
                null,
                true
        );
        definitions.put(Container.class, selfDefinition);
        instances.put(Container.class, this);
        singletonCreationOrder.add(this);
    }

    // =============================================================
    // REGISTRATION
    // =============================================================

    /**
     * Register metadata, not an object instance.
     *
     * This keeps the container focused on runtime behavior. ApplicationContext
     * builds the BeanDefinition; Container simply stores it.
     */
    public void register(BeanDefinition definition) {

        if (definition == null) {
            throw new IllegalArgumentException(
                    "BeanDefinition cannot be null"
            );
        }

        Class<?> beanClass = definition.getBeanClass();

        if (beanClass == null) {
            throw new IllegalArgumentException(
                    "BeanDefinition beanClass cannot be null"
            );
        }

        if (definitions.containsKey(beanClass)) {
            throw new RuntimeException(
                    "Bean already registered: "
                            + beanClass.getName()
            );
        }

        definitions.put(beanClass, definition);
    }

    /**
     * Return the registered metadata for an exact bean class, if present.
     */
    public BeanDefinition getBeanDefinition(Class<?> type) {
        return definitions.get(type);
    }

    /**
     * Register a processor that will participate in future bean creation.
     */
    public void addBeanPostProcessor(
            BeanPostProcessor processor) {
        postProcessors.add(processor);
    }


    // =============================================================
    // RESOLUTION
    // =============================================================

    /**
     * Resolve a bean without an explicit qualifier.
     */
    @Override
    public Object resolve(Class<?> type)
            throws Exception {
        Objects.requireNonNull(type, "Requested bean type must not be null");
        return resolve(type, null);
    }

    /**
     * Resolve a requested type to one concrete BeanDefinition.
     *
     * The order is deliberately explicit:
     *
     *   candidate selection -> scope check -> cycle check -> creation
     *   -> before-init processors -> initialization -> after-init processors
     *   -> singleton caching
     */
    public Object resolve(
            Class<?> requestedType,
            String qualifier)
            throws Exception {

        BeanDefinition definition =
                findCandidate(requestedType, qualifier);

        Class<?> actualType = definition.getBeanClass();

        // A singleton is reused once its instance has been created.
        if (definition.getScope() == Scope.SINGLETON
                && instances.containsKey(actualType)) {
            return instances.get(actualType);
        }

        // If the same bean appears again on the current resolution path,
        // the dependency graph contains a cycle such as A -> B -> A.
        if (resolutionStack.contains(actualType)) {

            StringBuilder path =
                    new StringBuilder();

            for (Class<?> type : resolutionStack) {

                if (path.length() > 0) {
                    path.append(" -> ");
                }

                path.append(type.getSimpleName());
            }

            path.append(" -> ")
                    .append(actualType.getSimpleName());

            throw new RuntimeException(
                    "Circular dependency detected: "
                            + path
            );
        }

        resolutionStack.addLast(actualType);

        try {
            Object object;

            // A definition either describes a normal class or an @Bean
            // factory method. Both paths eventually produce one object.
            if (definition.getFactoryMethod() != null) {
                object = createUsingFactoryMethod(definition);
            } else {
                object = createUsingConstructor(actualType);
            }
            // =============================================================
            // AWARE CALLBACKS
            // =============================================================
            // The instance now exists. Supply container-related information
            // before the ordinary initialization/post-processing callbacks.
            if (object instanceof BeanNameAware) {
                String beanName = definition.getBeanName();

                if (beanName == null || beanName.isBlank()) {
                    throw new IllegalStateException(
                            "BeanNameAware bean has no registered name: "
                                    + actualType.getName()
                    );
                }

                ((BeanNameAware) object).setBeanName(beanName);
            }

            if (object instanceof BeanResolverAware) {
                ((BeanResolverAware) object).setBeanResolver(this);
            }

            if (object instanceof ApplicationContextAware) {
                ((ApplicationContextAware) object)
                        .setApplicationContext(applicationContext);
            }

            // =============================================================
            // INITIALIZATION AND POST-PROCESSING
            // =============================================================
            // Post-processors may inspect or wrap the object before its
            // initialization callback runs.
            for (BeanPostProcessor processor : postProcessors) {
                object = processor.beforeInitialization(object);
            }

            if (object instanceof Initializable) {
                ((Initializable) object).initialize();
            }

            // A processor may return a different object (for example, a
            // proxy), so the value returned from afterInitialization is kept.
            for (BeanPostProcessor processor : postProcessors) {
                object = processor.afterInitialization(object);
            }

            // Only singleton results are retained. Prototype beans leave the
            // container after this method and will be created again later.
            if (definition.getScope() == Scope.SINGLETON) {
                instances.put(actualType, object);

                if (!singletonCreationOrder.contains(object)) {
                    singletonCreationOrder.add(object);
                }
            }

            return object;

        } finally {
            resolutionStack.removeLast();
        }
    }


    // =============================================================
    // CONSTRUCTOR CREATION
    // =============================================================

    private Object createUsingConstructor(Class<?> type)
            throws Exception {

        Constructor<?> constructor = findConstructor(type);
        Object[] arguments =
                resolveParameters(constructor.getParameters());

        constructor.setAccessible(true);
        return constructor.newInstance(arguments);
    }


    // =============================================================
    // @BEAN FACTORY-METHOD CREATION
    // =============================================================

    private Object createUsingFactoryMethod(
            BeanDefinition definition)
            throws Exception {

        Method factoryMethod = definition.getFactoryMethod();

        // Factory-method arguments use exactly the same dependency-resolution
        // pipeline as constructor arguments.
        Object[] arguments =
                resolveParameters(factoryMethod.getParameters());

        Object factoryObject = null;

        // A non-static @Bean method belongs to a @Configuration instance.
        // That configuration class is itself resolved through this container.
        if (!Modifier.isStatic(factoryMethod.getModifiers())) {
            factoryObject = resolve(
                    factoryMethod.getDeclaringClass()
            );
        }

        if (!factoryMethod.canAccess(factoryObject)) {
            factoryMethod.setAccessible(true);
        }

        return factoryMethod.invoke(factoryObject, arguments);
    }


    // =============================================================
    // DEPENDENCY ARGUMENT RESOLUTION
    // =============================================================

    /**
     * Build the argument array required by a constructor or factory method.
     *
     * Normal parameters are resolved immediately. Provider<T> is deliberately
     * different: it receives a lambda that resolves T only when get() is
     * called. This is what allows deferred access to prototype beans.
     */
    private Object[] resolveParameters(
            Parameter[] parameters)
            throws Exception {

        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            // ---------------------------------------------------------
            // Provider<T> — deferred dependency resolution
            // ---------------------------------------------------------
            if (parameter.getType() == Provider.class) {

                Type parameterizedType =
                        parameter.getParameterizedType();

                if (!(parameterizedType
                        instanceof ParameterizedType pt)) {
                    throw new RuntimeException(
                            "Provider must specify a target type."
                    );
                }

                Type[] actualTypeArguments =
                        pt.getActualTypeArguments();

                if (actualTypeArguments.length != 1) {
                    throw new RuntimeException(
                            "Provider must have exactly one type argument."
                    );
                }

                Type targetType = actualTypeArguments[0];

                if (!(targetType instanceof Class<?> targetClass)) {
                    throw new RuntimeException(
                            "Provider target must be a concrete class."
                    );
                }

                // Do NOT resolve targetClass here. The lambda stores the
                // instruction "resolve this later" and get() triggers it.
                arguments[i] =
                        (Provider<Object>) () ->
                                resolve(targetClass);

                // We have already supplied the argument, so skip normal
                // dependency resolution for Provider.class.
                continue;
            }

            // ---------------------------------------------------------
            // Normal dependency
            // ---------------------------------------------------------
            Class<?> dependencyType = parameter.getType();
            String dependencyQualifier = null;

            if (parameter.isAnnotationPresent(Qualifier.class)) {
                dependencyQualifier = parameter
                        .getAnnotation(Qualifier.class)
                        .value();
            }

            arguments[i] = resolve(
                    dependencyType,
                    dependencyQualifier
            );
        }

        return arguments;
    }


    // =============================================================
    // SHUTDOWN
    // =============================================================

    /**
     * Destroy singleton beans in reverse creation order.
     *
     * Reverse order mirrors the dependency direction: objects created later
     * are released before the objects they may depend on.
     */
    public void destroySingletons() {

        for (int i = singletonCreationOrder.size() - 1;
             i >= 0;
             i--) {

            Object object = singletonCreationOrder.get(i);

            if (object instanceof Destroyable) {
                ((Destroyable) object).destroy();
            }
        }

        singletonCreationOrder.clear();
        instances.clear();
    }


    // =============================================================
    // CANDIDATE SELECTION
    // =============================================================

    /**
     * Find the BeanDefinition that should satisfy a requested type.
     *
     * Selection rules:
     *   1. Find assignable candidates.
     *   2. If a qualifier was requested, match it exactly.
     *   3. If only one candidate remains, use it.
     *   4. Otherwise use exactly one @Primary candidate.
     *   5. If ambiguity remains, fail instead of guessing.
     */
    private BeanDefinition findCandidate(
            Class<?> requestedType,
            String qualifier) {

        List<BeanDefinition> candidates = new ArrayList<>();

        for (BeanDefinition definition : definitions.values()) {
            Class<?> beanClass = definition.getBeanClass();

            if (requestedType.isAssignableFrom(beanClass)) {
                candidates.add(definition);
            }
        }

        if (candidates.isEmpty()) {
            throw new RuntimeException(
                    "No bean found for "
                            + requestedType.getName()
            );
        }

        // An explicit qualifier always has priority over @Primary.
        if (qualifier != null) {
            for (BeanDefinition candidate : candidates) {
                if (qualifier.equals(candidate.getQualifier())) {
                    return candidate;
                }
            }

            throw new RuntimeException(
                    "No bean found with qualifier '"
                            + qualifier
                            + "' for "
                            + requestedType.getName()
            );
        }

        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        BeanDefinition primaryCandidate = null;

        for (BeanDefinition candidate : candidates) {
            if (!candidate.isPrimary()) {
                continue;
            }

            if (primaryCandidate != null) {
                throw new RuntimeException(
                        "Multiple @Primary beans found for "
                                + requestedType.getName()
                );
            }

            primaryCandidate = candidate;
        }

        if (primaryCandidate != null) {
            return primaryCandidate;
        }

        throw new RuntimeException(
                "Multiple beans found for "
                        + requestedType.getName()
                        + ". Use @Primary or "
                        + "@Qualifier."
        );
    }


    // =============================================================
    // CONSTRUCTOR SELECTION
    // =============================================================

    /**
     * Constructor policy for this learning container:
     *
     *   - Prefer exactly one @Inject constructor.
     *   - Otherwise allow a class with exactly one constructor.
     *   - Reject multiple constructors when none is explicitly selected.
     */
    private Constructor<?> findConstructor(Class<?> type) {

        Constructor<?>[] constructors =
                type.getDeclaredConstructors();

        Constructor<?> injectConstructor = null;

        for (Constructor<?> constructor : constructors) {
            if (!constructor.isAnnotationPresent(Inject.class)) {
                continue;
            }

            if (injectConstructor != null) {
                throw new RuntimeException(
                        "Multiple @Inject constructors found in "
                                + type.getName()
                );
            }

            injectConstructor = constructor;
        }

        if (injectConstructor != null) {
            return injectConstructor;
        }

        if (constructors.length == 1) {
            return constructors[0];
        }

        throw new RuntimeException(
                "Ambiguous constructors in "
                        + type.getName()
                        + ". Mark one with @Inject."
        );
    }

    /**
     * Connect this container to the context that owns it.
     *
     * This is called by ApplicationContext during construction, before
     * application beans are resolved.
     */
    public void setApplicationContext(
            ApplicationContext applicationContext) {

        if (applicationContext == null) {
            throw new IllegalArgumentException(
                    "ApplicationContext cannot be null"
            );
        }

        this.applicationContext = applicationContext;
    }
}
