package com.minispring.core;

import com.minispring.annotation.BeanScope;
import com.minispring.annotation.Component;
import com.minispring.annotation.Inject;
import com.minispring.annotation.Primary;
import com.minispring.annotation.Qualifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContainerTest {

    @Test
    void singletonBeansAreCached() throws Exception {
        Container container = new Container();
        container.register(definition(SingletonService.class, Scope.SINGLETON));

        SingletonService first =
                (SingletonService) container.resolve(SingletonService.class);
        SingletonService second =
                (SingletonService) container.resolve(SingletonService.class);

        assertSame(first, second);
    }

    @Test
    void prototypeBeansAreCreatedForEachResolution() throws Exception {
        Container container = new Container();
        container.register(definition(PrototypeService.class, Scope.PROTOTYPE));

        PrototypeService first =
                (PrototypeService) container.resolve(PrototypeService.class);
        PrototypeService second =
                (PrototypeService) container.resolve(PrototypeService.class);

        assertNotSame(first, second);
    }

    @Test
    void qualifierSelectsTheRequestedImplementation() throws Exception {
        Container container = new Container();
        container.register(definition(QualifiedService.class, Scope.SINGLETON));
        container.register(definition(RedPayment.class, Scope.SINGLETON));
        container.register(definition(BluePayment.class, Scope.SINGLETON));

        QualifiedService service =
                (QualifiedService) container.resolve(QualifiedService.class);

        assertInstanceOf(BluePayment.class, service.payment);
    }

    @Test
    void primarySelectsTheDefaultImplementation() throws Exception {
        Container container = new Container();
        container.register(definition(PrimaryService.class, Scope.SINGLETON));
        container.register(definition(PrimaryPayment.class, Scope.SINGLETON));
        container.register(definition(SecondaryPayment.class, Scope.SINGLETON));

        PrimaryService service =
                (PrimaryService) container.resolve(PrimaryService.class);

        assertInstanceOf(PrimaryPayment.class, service.payment);
    }

    @Test
    void circularDependenciesAreRejected() {
        Container container = new Container();
        container.register(definition(CycleA.class, Scope.SINGLETON));
        container.register(definition(CycleB.class, Scope.SINGLETON));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> container.resolve(CycleA.class)
        );

        assertTrue(exception.getMessage().contains("CycleA"));
        assertTrue(exception.getMessage().contains("CycleB"));
    }

    private static BeanDefinition definition(
            Class<?> type,
            Scope scope) {
        return new BeanDefinition(
                type,
                scope,
                type.getAnnotation(Qualifier.class) == null
                        ? null
                        : type.getAnnotation(Qualifier.class).value(),
                type.isAnnotationPresent(Primary.class)
        );
    }

    interface Payment {
    }

    @Component
    static class SingletonService {
    }

    @Component
    @BeanScope(Scope.PROTOTYPE)
    static class PrototypeService {
    }

    @Component
    static class QualifiedService {
        final Payment payment;

        @Inject
        QualifiedService(@Qualifier("blue") Payment payment) {
            this.payment = payment;
        }
    }

    @Component
    @Qualifier("red")
    static class RedPayment implements Payment {
    }

    @Component
    @Qualifier("blue")
    static class BluePayment implements Payment {
    }

    @Component
    static class PrimaryService {
        final Payment payment;

        @Inject
        PrimaryService(Payment payment) {
            this.payment = payment;
        }
    }

    @Component
    @Primary
    static class PrimaryPayment implements Payment {
    }

    @Component
    static class SecondaryPayment implements Payment {
    }

    @Component
    static class CycleA {
        @Inject
        CycleA(CycleB ignored) {
        }
    }

    @Component
    static class CycleB {
        @Inject
        CycleB(CycleA ignored) {
        }
    }
}
