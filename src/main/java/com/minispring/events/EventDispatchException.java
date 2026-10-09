package com.minispring.events;

/** Raised when a listener cannot be resolved or invoked successfully. */
public final class EventDispatchException extends RuntimeException {

    public EventDispatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
