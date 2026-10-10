# MiniSpring

**MiniSpring is an educational Java 17 project that rebuilds the core ideas behind a Spring-style IoC container from first principles.**

It is designed to help you understand *why* a framework needs each mechanism, *what* that mechanism is responsible for, and *how* the pieces work together.

> **Status:** Spring Core learning project in active development. Implemented areas include dependency injection, component scanning, bean scopes and lifecycle, factory methods, conditional registration, application events, Aware callbacks, and an educational interface-based AOP pipeline.

## Start here

- **[MiniSpring Internals](MINISPRING_INTERNALS.md)** — detailed explanation of the architecture, components, algorithms, lifecycle, flows, and current limitations.
- **[MiniSpring Demo & Verification Guide](MINISPRING_DEMO.md)** — how to run the examples, understand the demo, run tests, and verify features.
- **[Architecture Notes](docs/architecture.md)** — concise responsibility boundaries and architecture diagrams.

## Why build a mini container?

Spring lets an application request a managed object without manually constructing every dependency. Underneath that convenience, a container must answer questions such as:

- Who creates objects, and who owns their lifecycle?
- How does a constructor receive the objects it needs?
- How does the framework choose between multiple implementations?
- How can it discover components and factory methods?
- What is the difference between a bean definition and a bean instance?
- When should initialization callbacks and post-processors run?
- How can the container detect circular dependencies?
- How can profiles and properties decide whether a bean participates?
- How can an event reach the right listener?

MiniSpring implements simplified versions of these mechanisms so they can be studied and tested independently.

## Current capabilities

- Inversion of Control and constructor dependency injection
- `@Inject`, `@Qualifier`, and `@Primary`
- `Provider<T>` for deferred dependency resolution
- Classpath scanning, `@Component`, `@Configuration`, and `@Bean`
- `BeanDefinition` metadata, singleton/prototype scopes, and circular-dependency detection
- `Initializable`, `Destroyable`, `BeanPostProcessor`, and `BeanFactoryPostProcessor`
- Profiles and property-based conditional registration
- Synchronous application events through `@EventListener`
- `BeanNameAware`, `BeanResolverAware`, and `ApplicationContextAware` callbacks
- Interface-based AOP using JDK dynamic proxies
- Method matchers and ordered interceptor chains
- Logging and timing interceptor examples
- Automatic proxy creation through `AopBeanPostProcessor` when configured bindings match
- JUnit 5 tests for the current implementation

The two interfaces `BeanResolverAware` and this project's `ApplicationContextAware` are MiniSpring learning implementations. `BeanNameAware` follows Spring's interface name; MiniSpring is not a drop-in implementation of Spring's APIs.

## Requirements

- Java 17 or newer
- Maven

## Build and test

From the directory containing `pom.xml`:

```bash
mvn test
mvn clean verify
```

Run the example application:

```bash
mvn package
java -jar target/mini-spring-container-0.1.0-SNAPSHOT.jar
```

## Package map

```text
src/main/java/com/minispring/
├── annotation/   Annotations that describe components and configuration
├── condition/    Rules used to decide whether definitions are eligible
├── context/      ApplicationContext and Environment
├── core/         BeanDefinition, Container, scopes, resolver, Provider
├── demo/         Small runnable examples
├── events/       Event listener metadata, registry, and publisher
├── lifecycle/    Initialization, destruction, awareness, and processor contracts
└── scanner/      Classpath and semantic scanners

src/test/java/com/minispring/
└── ...           Automated tests for framework behavior
```

## Learning roadmap

```text
IoC and dependency injection                 ✓
Discovery and bean definitions               ✓
Lifecycle, scopes, and post-processors        ✓
Factory methods and conditional registration  ✓
Application events                            ✓
Aware callbacks                               ✓
AOP fundamentals and interceptor chains        ✓
JDK dynamic proxies and automatic proxying     ✓
Class-based proxies / CGLIB concepts           → future learning stage
Spring Core internals
Connect the concepts to Spring Boot
```

## Scope and limitations

MiniSpring is an educational framework, **not a replacement for Spring and not intended as a production dependency-injection library**. It deliberately implements a small subset of Spring's behavior. For example, the current classpath scanner is simple and directory-oriented, bean registrations are keyed by class (so multiple separately named definitions of the same class are not supported), event delivery is synchronous and matches the exact event class, and AOP currently supports interface-based JDK dynamic proxies rather than class-based proxies.

### AOP behavior and limitations

Configure AOP by passing a list of `InterceptorBinding` objects to the three-argument `ApplicationContext` constructor. Each binding combines a `MethodMatcher` with a `MethodInterceptor`. Matching bindings run in their configured order; each interceptor calls `invocation.proceed()` to continue the chain.

```java
List<InterceptorBinding> bindings = List.of(
    new InterceptorBinding(
        new MethodNameMatcher("placeOrder"),
        new LoggingInterceptor()
    )
);

try (ApplicationContext context = new ApplicationContext(
        "com.example.app", new Environment(), bindings)) {
    OrderService service = context.getBean(OrderService.class);
    service.placeOrder();
}
```

The example assumes `OrderService` is an interface and a matching bean is discovered under the supplied package. See `com.minispring.demo.aop` for runnable examples and the exact project setup.

- **Interface-based only:** a JDK proxy implements interfaces; it is not an instance of the concrete target class. Retrieve proxied beans through their interface.
- **Self-invocation:** a target method calling another method on `this` bypasses the proxy, so the inner call is not intercepted.
- **Matching scope:** the current `MethodNameMatcher` matches by method name, so overloaded methods with the same name also match.
- **No class proxying:** CGLIB-style subclass proxies, introductions, annotation-driven pointcuts, and full Spring AOP compatibility are not implemented.
- **Post-processing:** AOP proxying happens after initialization for beans created after the AOP post-processor is registered. Existing infrastructure beans are not retroactively proxied.

These are deliberate boundaries of this learning implementation, not claims of complete Spring compatibility. See [MiniSpring Internals](MINISPRING_INTERNALS.md) for the broader architecture and other limitations.

## Learning method

Each feature is approached in this order:

1. Start with a concrete problem.
2. Reason about a simple solution.
3. Identify where that solution breaks down.
4. Introduce the smallest useful abstraction.
5. Implement it in MiniSpring.
6. Add tests and document the behavior.
7. Compare the simplified design with real Spring.

The repository is both a working codebase and a record of that learning process.
