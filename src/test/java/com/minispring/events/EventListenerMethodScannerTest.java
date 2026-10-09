package com.minispring.events;

import com.minispring.annotation.EventListener;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventListenerMethodScannerTest {

    static final class OrderCreated {
    }

    static final class PaymentFailed {
    }

    static class EmailService {
        @EventListener
        public void onOrderCreated(OrderCreated event) {
        }

        public void unrelatedMethod() {
        }
    }

    static class MultipleListeners {
        @EventListener
        public void onOrderCreated(OrderCreated event) {
        }

        @EventListener
        public void onPaymentFailed(PaymentFailed event) {
        }
    }

    static class InvalidListener {
        @EventListener
        public void handle() {
        }
    }

    @Test
    void discoversAnnotatedMethodAndEventType() {
        List<EventListenerDefinition> definitions =
                new EventListenerMethodScanner().scan(EmailService.class);

        assertEquals(1, definitions.size());
        assertEquals(EmailService.class, definitions.get(0).getBeanClass());
        assertEquals(OrderCreated.class, definitions.get(0).getEventType());
        assertEquals("onOrderCreated", definitions.get(0).getMethod().getName());
    }

    @Test
    void supportsMultipleListenerMethodsOnOneClass() {
        List<EventListenerDefinition> definitions =
                new EventListenerMethodScanner().scan(MultipleListeners.class);

        assertEquals(2, definitions.size());
        assertTrue(definitions.stream().anyMatch(d -> d.getEventType() == OrderCreated.class));
        assertTrue(definitions.stream().anyMatch(d -> d.getEventType() == PaymentFailed.class));
    }

    @Test
    void ignoresMethodsWithoutAnnotation() {
        List<EventListenerDefinition> definitions =
                new EventListenerMethodScanner().scan(EmailService.class);

        assertFalse(definitions.stream().anyMatch(
                d -> d.getMethod().getName().equals("unrelatedMethod")
        ));
    }

    @Test
    void rejectsListenerWithoutExactlyOneParameter() {
        assertThrows(IllegalArgumentException.class,
                () -> new EventListenerMethodScanner().scan(InvalidListener.class));
    }
}
