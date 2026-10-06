# MiniSpring — Educational Dependency Injection Container

MiniSpring is a small Java framework built from first principles to understand the core ideas behind a Spring-style IoC container.

The project is intentionally implemented without depending on Spring itself. Instead, it reconstructs the important mechanisms step by step: object creation, dependency injection, bean metadata, component discovery, scopes, lifecycle callbacks, post-processors, factory methods, circular-dependency detection, and environment-based conditional registration.

> **Status: In Development**
>
> The current implementation covers the container foundations and early Spring Core concepts. Application events are the next feature being developed, followed by AOP/proxies and deeper Spring Core internals.

---

## Why this project exists

Frameworks such as Spring hide a large amount of infrastructure behind simple annotations. MiniSpring removes that abstraction layer so the underlying problems can be studied directly.

The project is driven by questions such as:

- Who creates application objects?
- How does one object receive another object as a dependency?
- How can a container discover classes automatically?
- How does it choose between multiple implementations of an interface?
- How are singleton and prototype lifetimes different?
- How can a container detect circular dependencies?
- Why is metadata separated from actual object instances?
- How can factory methods create managed objects?
- Where should lifecycle callbacks and post-processors run?
- How can configuration profiles and properties decide which beans exist?

The goal is understanding the machinery, not reproducing Spring's entire feature set.

---

## Current feature set

### Core container

- Inversion of Control (IoC)
- Constructor-based dependency injection
- Runtime dependency resolution using reflection
- `@Inject` constructor selection
- Interface-to-implementation resolution
- `@Qualifier` based candidate selection
- `@Primary` based default candidate selection
- Circular dependency detection with a resolution path
- Lazy/deferred dependency lookup through `Provider<T>`

### Bean lifecycle

- Bean definitions separated from bean instances
- Singleton scope
- Prototype scope
- Constructor creation
- Initialization callbacks
- Destruction callbacks
- `BeanPostProcessor`
- `BeanFactoryPostProcessor`
- Singleton destruction in reverse creation order
- Post-processor replacement support, including future proxy use cases

### Discovery and configuration

- Classpath discovery
- Component scanning
- Configuration class scanning
- `@Bean` factory methods
- Factory-method dependency injection
- Static and instance factory-method support
- `@BeanScope`
- `@Qualifier` on beans, factory methods, and injection points
- `@Primary` on beans and factory methods

### Environment and conditions

- Runtime `Environment`
- Active profiles
- String configuration properties
- `@Profile`
- `@ConditionalOnProperty`
- Composable `Condition` objects
- Condition evaluation before bean registration
- Type-level and `@Bean` method-level conditions

---

## Architecture

The project intentionally separates discovery, application orchestration, metadata, and runtime creation.

```text
                       ApplicationContext
                              |
             +----------------+----------------+
             |                                 |
        Discovery                         Environment
             |                                 |
    +--------+---------+              +--------+--------+
    |                  |              |                 |
ClassPathScanner  Semantic Scanners  Profiles       Properties
    |                  |
    +--------+---------+
             |
       BeanDefinitions
             |
             v
         ApplicationContext
             |
             v
          Container
             |
    +--------+---------+----------------+
    |                  |                |
Candidate Selection  Creation       Lifecycle
    |                  |                |
Qualifier/Primary  Constructor/@Bean  Processors
    |                  |                |
    +------------------+----------------+
                       |
                       v
                  Managed Beans
```

### Package structure

```text
src/main/java/com/minispring/
├── annotation/       Framework annotations
├── condition/        Conditional registration rules
├── context/          ApplicationContext and Environment
├── core/             Bean metadata, container, scopes, Provider
├── lifecycle/        Lifecycle and post-processing contracts
├── scanner/          Classpath and semantic discovery
└── demo/             Small examples used to exercise the framework
```

The `demo` package contains examples only. The framework itself is kept under the other packages.

---

## Bean creation flow

A simplified bean resolution currently follows this sequence:

```text
getBean(Type)
     |
     v
Find candidate
     |
     +--> qualifier
     |
     +--> single candidate
     |
     +--> primary candidate
     |
     v
Check singleton cache
     |
     v
Check active resolution path
     |
     v
Resolve dependencies
     |
     v
Create object
     |
     v
beforeInitialization
     |
     v
initialize()
     |
     v
afterInitialization
     |
     v
Cache if singleton
     |
     v
Return managed object
```

This ordering is deliberately explicit because lifecycle ordering becomes important when proxies and AOP are introduced later.

---

## Scopes

MiniSpring currently supports two scopes:

### Singleton

One managed instance is cached by the container and reused for subsequent resolutions.

```text
resolve(A) -> A1
resolve(A) -> A1
resolve(A) -> A1
```

### Prototype

A new instance is created for every resolution.

```text
resolve(A) -> A1
resolve(A) -> A2
resolve(A) -> A3
```

A singleton that directly receives a prototype still holds the prototype created during its own construction. `Provider<T>` provides deferred access when repeated prototype creation is required.

---

## Circular dependency handling

The container maintains the **active resolution path**, rather than treating the entire dependency graph as a cycle.

For example:

```text
A -> B -> C -> A
```

produces an error similar to:

```text
Circular dependency detected: A -> B -> C -> A
```

`Provider<T>` introduces a deferred dependency edge. The dependency is resolved only when `Provider.get()` is called, which is why it can break a construction-time cycle in appropriate designs.

---

## Profiles and conditional beans

The `Environment` stores runtime configuration:

```java
Environment environment = new Environment();
environment.addProfile("dev");
environment.setProperty("feature.enabled", "true");
```

A component can then be conditional:

```java
@Component
@Profile("dev")
public class DevOnlyFeature {
}
```

or:

```java
@Component
@ConditionalOnProperty(
        name = "feature.enabled",
        havingValue = "true")
public class PropertyFeature {
}
```

Conditions are evaluated by `ApplicationContext` before definitions are registered with `Container`. This keeps environment decisions outside the dependency-resolution engine.

---

## Factory methods

Configuration classes can define managed objects through `@Bean` methods:

```java
@Configuration
public class AppConfig {

    @Bean
    public PaymentGateway paymentGateway() {
        return new StripeGateway();
    }
}
```

Factory-method parameters are resolved through the same dependency-resolution mechanism used for constructor injection.

The framework also keeps factory metadata separate from the created object through `BeanDefinition`.

---

## Bean post-processing

`BeanPostProcessor` provides hooks around initialization:

```text
construct
   |
beforeInitialization
   |
initialize
   |
afterInitialization
   |
managed instance
```

A post-processor may return a different object during the final stage. This is intentionally important for the next part of the project: proxy-based features and AOP.

---

## Testing

The repository includes tests covering important container behavior, including:

- Singleton reuse
- Prototype creation
- Qualifier selection
- Primary selection
- Circular dependency detection
- Profile-based registration
- Property-based registration

The project uses JUnit 5.

Run the test suite with:

```bash
mvn test
```

Run the demonstration application with:

```bash
mvn package && java -jar target/mini-spring-container-0.1.0-SNAPSHOT.jar
```

Build the project with:

```bash
mvn clean verify
```

The project targets **Java 17** and uses Maven.

---

## Development roadmap

The project follows a controlled Spring Core learning sequence.

```text
Object Lifecycle                    ✓
Inversion of Control                ✓
Dependency Injection                ✓
Tiny DI Container                   ✓
Component Scanning                  ✓
Bean Lifecycle                      ✓
ApplicationContext                  ✓
Bean Scopes                         ✓
Configuration & Properties          ✓
BeanPostProcessor                   ✓
Factory Methods                     ✓
Profiles & Conditional Beans        ✓

Events & Application Events        → Next
Spring Aware Interfaces
AOP Fundamentals
Proxies
JDK Dynamic Proxy / CGLIB
Spring Core Internals
Putting Everything Together

                ↓
           Spring Boot
```

The project is intentionally not trying to implement every Spring feature at once. Each feature is added after the underlying problem and design have been understood.

---

## What this project is not

MiniSpring is an educational framework implementation. It is **not** a replacement for Spring and should not be used as a production dependency-injection framework.

Important production features are intentionally simplified or not yet implemented, including robust JAR/module classpath scanning, advanced dependency semantics, concurrency guarantees, configuration binding, asynchronous events, full proxy infrastructure, AOP advice, and Spring Boot auto-configuration.

Those omissions are deliberate: the project is being developed incrementally so that each subsystem can be understood and tested in isolation.

---

## Learning approach

The implementation follows a problem-first approach:

```text
Problem
   ↓
Why does the problem exist?
   ↓
What would a simple solution look like?
   ↓
Where does that solution break?
   ↓
What abstraction naturally emerges?
   ↓
Implement the abstraction
   ↓
Test the behavior
   ↓
Compare with production frameworks
```

This makes the repository both a working Java project and a record of how a dependency-injection framework can be constructed from first principles.

---

## License

This project is provided for educational and portfolio purposes.
