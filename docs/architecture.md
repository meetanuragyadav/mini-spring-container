# Architecture Notes

## Responsibility boundaries

MiniSpring separates four major concerns.

### 1. Discovery

`ClassPathScanner` finds classes available under the requested package.

`ComponentScanner` and `ConfigurationScanner` perform semantic filtering.

Discovery does not create beans.

### 2. Application orchestration

`ApplicationContext` turns discovered classes and methods into `BeanDefinition` objects, evaluates environment conditions, registers eligible definitions, installs processors, and exposes the application-level API.

### 3. Runtime container

`Container` owns bean resolution and creation:

- candidate selection
- constructor/factory invocation
- dependency resolution
- scope handling
- lifecycle callbacks
- post-processing
- circular-dependency detection
- singleton destruction

### 4. Metadata

`BeanDefinition` describes a bean without being the bean itself.

```text
BeanDefinition
├── bean class
├── scope
├── qualifier
├── primary flag
├── factory method
└── conditions
```

This separation is important because the framework must often make decisions about a bean before an object exists.

## Resolution path vs dependency graph

The container's `resolutionStack` represents only the beans currently being constructed.

For:

```text
A -> B -> C -> A
```

the active path becomes:

```text
[A]
[A, B]
[A, B, C]
```

When resolving `A` again, `A` is already on the active path, so the container reports a cycle.

This is different from storing every dependency in a global set. A bean can legitimately appear in different independent branches of a larger dependency graph.
