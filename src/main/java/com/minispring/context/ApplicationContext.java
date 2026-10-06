package com.minispring.context;

import com.minispring.annotation.*;
import com.minispring.condition.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;
import com.minispring.scanner.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The entry point of the mini Spring container.
 *
 * <p>ApplicationContext is responsible for orchestration, not object
 * creation. It discovers classes, turns discovered metadata into
 * BeanDefinitions, evaluates environment conditions, registers eligible
 * definitions with Container, and installs post-processors.</p>
 *
 * <p>The important architectural boundary is:</p>
 *
 * <pre>
 * Discovery -> metadata/conditions -> ApplicationContext -> Container
 * </pre>
 *
 * <p>The scanners discover what exists. The context decides what should
 * participate in this application environment. Container owns how an
 * eligible bean is resolved and created.</p>
 */
public class ApplicationContext implements AutoCloseable {

    private final Container container;
    private final Environment environment;

    /**
     * Build a context using a fresh, empty environment.
     *
     * <p>Regular application beans remain lazy until requested through
     * {@link #getBean(Class)}.</p>
     */
    public ApplicationContext(String packageName)
            throws Exception {
        this(packageName, new Environment());
    }

    /**
     * Build a context using the supplied runtime environment.
     *
     * <p>Profiles and properties are evaluated while bean definitions are
     * being built. Therefore the environment should be configured before
     * constructing the context.</p>
     */
    public ApplicationContext(
            String packageName,
            Environment environment)
            throws Exception {

        if (environment == null) {
            throw new IllegalArgumentException(
                    "Environment cannot be null"
            );
        }

        this.container = new Container();
        this.environment = environment;

        // =============================================================
        // PHASE 0 — DISCOVERY
        // =============================================================
        // First find every class under the requested package. Then narrow
        // that result into components and @Bean methods. No bean instances
        // are created during discovery.

        ClassPathScanner classPathScanner =
                new ClassPathScanner();

        List<Class<?>> classes =
                classPathScanner.scan(packageName);

        ComponentScanner componentScanner =
                new ComponentScanner();

        List<Class<?>> components =
                componentScanner.scan(classes);

        ConfigurationScanner configurationScanner =
                new ConfigurationScanner();

        List<BeanMethod> beanMethods =
                configurationScanner.scan(classes);

        // =============================================================
        // PHASE 1 — BUILD AND REGISTER BEAN DEFINITIONS
        // =============================================================
        // Discovery tells us which classes/methods are candidates.
        // ApplicationContext converts those candidates into metadata.
        // Conditions are evaluated before Container sees the definition.

        // @Component classes become class-backed bean definitions.
        for (Class<?> component : components) {
            BeanDefinition definition =
                    createComponentDefinition(component);

            if (conditionsMatch(definition)) {
                container.register(definition);
            }
        }

        // @Configuration classes are managed beans because an instance may be
        // needed to invoke their non-static @Bean methods. If a configuration
        // class is not eligible, its @Bean methods are not eligible either.
        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(Configuration.class)) {
                continue;
            }

            BeanDefinition definition =
                    createComponentDefinition(clazz);

            if (conditionsMatch(definition)) {
                container.register(definition);
            }
        }

        // Each @Bean method becomes a bean definition whose factory method
        // tells Container how to create the returned object.
        for (BeanMethod beanMethod : beanMethods) {
            Method method = beanMethod.getMethod();
            Class<?> configurationClass =
                    beanMethod.getConfigurationClass();

            // The configuration class controls whether its factory methods
            // participate. Method-level conditions can further restrict an
            // individual @Bean method.
            BeanDefinition configurationDefinition =
                    container.getBeanDefinition(configurationClass);

            if (configurationDefinition == null) {
                continue;
            }

            BeanDefinition definition =
                    createBeanMethodDefinition(method);

            if (conditionsMatch(definition)) {
                container.register(definition);
            }
        }

        // =============================================================
        // PHASE 2 — BEAN FACTORY POST-PROCESSORS
        // =============================================================
        // These processors can inspect/change bean metadata before normal
        // application beans are resolved.

        for (Class<?> component : components) {
            if (!BeanFactoryPostProcessor.class
                    .isAssignableFrom(component)) {
                continue;
            }

            if (container.getBeanDefinition(component) == null) {
                continue;
            }

            BeanFactoryPostProcessor processor =
                    (BeanFactoryPostProcessor)
                            container.resolve(component);

            processor.postProcessBeanFactory(container);
        }

        // =============================================================
        // PHASE 3 — BEAN POST-PROCESSORS
        // =============================================================
        // BeanPostProcessors participate around bean initialization. They are
        // created first and then registered with Container so subsequently
        // created application beans pass through the processor chain.

        for (Class<?> component : components) {
            if (!BeanPostProcessor.class
                    .isAssignableFrom(component)) {
                continue;
            }

            if (container.getBeanDefinition(component) == null) {
                continue;
            }

            BeanPostProcessor processor =
                    (BeanPostProcessor)
                            container.resolve(component);

            container.addBeanPostProcessor(processor);
        }
    }

    /**
     * Converts a discovered component/configuration class into metadata.
     */
    private BeanDefinition createComponentDefinition(
            Class<?> component) {

        Scope scope = Scope.SINGLETON;

        if (component.isAnnotationPresent(BeanScope.class)) {
            scope = component
                    .getAnnotation(BeanScope.class)
                    .value();
        }

        String qualifier = null;

        if (component.isAnnotationPresent(Qualifier.class)) {
            qualifier = component
                    .getAnnotation(Qualifier.class)
                    .value();
        }

        boolean primary =
                component.isAnnotationPresent(Primary.class);

        BeanDefinition definition =
                new BeanDefinition(
                        component,
                        scope,
                        qualifier,
                        primary
                );

        addTypeConditions(definition, component);
        return definition;
    }

    /**
     * Converts an @Bean method into factory-method metadata.
     */
    private BeanDefinition createBeanMethodDefinition(
            Method method) {

        Scope scope = Scope.SINGLETON;

        if (method.isAnnotationPresent(BeanScope.class)) {
            scope = method
                    .getAnnotation(BeanScope.class)
                    .value();
        }

        String qualifier = null;

        if (method.isAnnotationPresent(Qualifier.class)) {
            qualifier = method
                    .getAnnotation(Qualifier.class)
                    .value();
        }

        boolean primary =
                method.isAnnotationPresent(Primary.class);

        BeanDefinition definition =
                new BeanDefinition(
                        method.getReturnType(),
                        scope,
                        qualifier,
                        primary
                );

        definition.setFactoryMethod(method);
        addMethodConditions(definition, method);

        return definition;
    }

    /**
     * Convert type-level condition annotations into Condition objects.
     */
    private void addTypeConditions(
            BeanDefinition definition,
            Class<?> type) {

        Profile profile =
                type.getAnnotation(Profile.class);

        if (profile != null) {
            definition.addCondition(
                    new ProfileCondition(profile.value())
            );
        }

        ConditionalOnProperty property =
                type.getAnnotation(ConditionalOnProperty.class);

        if (property != null) {
            definition.addCondition(
                    new PropertyCondition(
                            property.name(),
                            property.havingValue()
                    )
            );
        }
    }

    /**
     * Convert method-level condition annotations into Condition objects.
     */
    private void addMethodConditions(
            BeanDefinition definition,
            Method method) {

        Profile profile =
                method.getAnnotation(Profile.class);

        if (profile != null) {
            definition.addCondition(
                    new ProfileCondition(profile.value())
            );
        }

        ConditionalOnProperty property =
                method.getAnnotation(ConditionalOnProperty.class);

        if (property != null) {
            definition.addCondition(
                    new PropertyCondition(
                            property.name(),
                            property.havingValue()
                    )
            );
        }
    }

    /**
     * A definition is eligible only when every attached condition matches.
     */
    private boolean conditionsMatch(
            BeanDefinition definition) {

        for (Condition condition : definition.getConditions()) {
            if (!condition.matches(environment)) {
                return false;
            }
        }

        return true;
    }

    // =============================================================
    // PUBLIC API
    // =============================================================

    /**
     * Resolve a bean by type. Candidate selection and creation are delegated
     * to Container so the context remains an orchestration layer.
     */
    public <T> T getBean(Class<T> type)
            throws Exception {

        return type.cast(container.resolve(type));
    }

    /**
     * Resolve a bean by type and an explicit @Qualifier value.
     */
    public <T> T getBean(
            Class<T> type,
            String qualifier)
            throws Exception {

        return type.cast(
                container.resolve(type, qualifier)
        );
    }

    /**
     * Return the environment used to configure this context.
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Shut down the context. Container destroys managed singleton beans in
     * reverse creation order so dependent resources are released last.
     */
    @Override
    public void close() {
        container.destroySingletons();
    }
}
