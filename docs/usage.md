# Usage Guide

This guide covers building the project, running the included examples, and configuring the main features.

## Build and verify

Requirements: JDK 17 or newer and Apache Maven.

```bash
mvn clean verify
```

Run the default container demonstration:

```bash
mvn package
java -jar target/mini-spring-container-0.1.0-SNAPSHOT.jar
```

The default example demonstrates constructor injection, qualifiers, configuration factory methods, environment conditions, application events, and prototype instances.

## Configure the environment

Set properties and profiles before creating `ApplicationContext`, because conditions are evaluated while bean definitions are built.

```java
Environment environment = new Environment();
environment.addProfile("dev");
environment.setProperty("feature.enabled", "true");

try (ApplicationContext context =
         new ApplicationContext("com.example.app", environment)) {
    // Request managed beans from the context.
}
```

Replace `com.example.app` with the package containing the application's components and configuration classes.

## Constructor injection and qualifiers

A component declares dependencies in its constructor. When multiple implementations are eligible, `@Qualifier` selects the intended bean; `@Primary` can identify a preferred candidate.

```java
@Component
public class CheckoutService {
    private final PaymentGateway gateway;

    @Inject
    public CheckoutService(@Qualifier("stripe") PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

The named qualifier must correspond to the metadata registered for the implementation. See the classes in `com.minispring.demo` for a complete runnable example.

## Factory methods

A class annotated with `@Configuration` can expose a bean through an `@Bean` method:

```java
@Configuration
public class PaymentConfiguration {
    @Bean
    public PaymentGateway paymentGateway() {
        return new StripeGateway();
    }
}
```

Factory-method parameters are resolved through the container. The returned object then participates in the normal bean lifecycle and post-processing pipeline.

## Application events

Annotate a listener method with `@EventListener` and publish an event through `EventPublisher` or `ApplicationContext.publishEvent(...)`. Event delivery is synchronous in the current implementation, so listeners run as part of the publishing call.

## Container-integrated AOP

The included example is `com.minispring.demo.aopcontainer.AopContainerDemo`. Run it after compiling the project:

```bash
mvn package
java -cp target/classes com.minispring.demo.aopcontainer.AopContainerDemo
```

It configures method-name matchers for `processPayment`, pairs them with logging and timing interceptors, and passes those bindings to `ApplicationContext`.

A binding has this general shape:

```java
new InterceptorBinding(
    new MethodNameMatcher("processPayment"),
    new LoggingInterceptor()
);
```

The proxy intercepts calls made through the `PaymentOperations` interface. Matching interceptors execute in the configured order, and each interceptor calls `invocation.proceed()` to continue the chain. The final continuation invokes the target method.

### AOP limitations

- Proxied objects must be requested through a supported interface; the proxy is not the concrete target class.
- A method calling another method directly on `this` bypasses the proxy.
- The current matcher selects by method name, including same-name overloads.
- Class-based proxies and full Spring AOP compatibility are not implemented.

## Tests

Run the complete suite with:

```bash
mvn clean verify
```

Tests are organized under `src/test/java/com/minispring` by subsystem. They cover container behavior, lifecycle integration, event discovery and dispatch, proxy behavior, interceptor execution, and AOP integration.
