package com.minispring.events;

import com.minispring.annotation.EventListener;
import com.minispring.core.BeanDefinition;
import com.minispring.core.Container;
import com.minispring.core.Scope;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class EventPublisherTest {

    static final class OrderCreated {
        private final String id;

        OrderCreated(String id) {
            this.id = id;
        }
    }

    static class Listener {
        private String receivedId;
        private int callCount;

        @EventListener
        public void handle(OrderCreated event) {
            receivedId = event.id;
            callCount++;
        }

        String getReceivedId() { return receivedId; }
        int getCallCount() { return callCount; }
    }

    @Test
    void resolvesManagedBeanAndInvokesListener() throws Exception {
        Container container = new Container();
        container.register(new BeanDefinition(Listener.class, Scope.SINGLETON, null, false));

        EventRegistry registry = new EventRegistry();
        Method method = Listener.class.getDeclaredMethod("handle", OrderCreated.class);
        registry.register(new EventListenerDefinition(Listener.class, method, OrderCreated.class));

        EventPublisher publisher = new EventPublisher(registry, container);
        publisher.publishEvent(new OrderCreated("order-7"));

        Listener listener = (Listener) container.resolve(Listener.class);
        assertEquals("order-7", listener.getReceivedId());
        assertEquals(1, listener.getCallCount());
    }

    @Test
    void doesNothingWhenNoListenerMatchesEventType() {
        Container container = new Container();
        EventPublisher publisher = new EventPublisher(new EventRegistry(), container);
        assertDoesNotThrow(() -> publisher.publishEvent(new OrderCreated("no-listener")));
    }

    @Test
    void rejectsNullEvents() {
        EventPublisher publisher = new EventPublisher(new EventRegistry(), new Container());
        assertThrows(NullPointerException.class, () -> publisher.publishEvent(null));
    }
    static class FailingListener {
        @EventListener
        public void handle(OrderCreated event) {
            throw new IllegalStateException("listener failed");
        }
    }

    @Test
    void wrapsListenerFailureAndPreservesOriginalCause() throws Exception {
        Container container = new Container();
        container.register(new BeanDefinition(FailingListener.class, Scope.SINGLETON, null, false));

        EventRegistry registry = new EventRegistry();
        Method method = FailingListener.class.getDeclaredMethod("handle", OrderCreated.class);
        registry.register(new EventListenerDefinition(FailingListener.class, method, OrderCreated.class));

        EventPublisher publisher = new EventPublisher(registry, container);
        EventDispatchException exception = assertThrows(
                EventDispatchException.class,
                () -> publisher.publishEvent(new OrderCreated("failure"))
        );

        assertEquals("listener failed", exception.getCause().getMessage());
    }

}
