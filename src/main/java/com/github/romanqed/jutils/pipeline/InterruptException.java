package com.github.romanqed.jutils.pipeline;

/**
 * An exception that is an interrupt marker for the pipeline.
 */
public class InterruptException extends RuntimeException {
    private final Object body;

    public InterruptException(String message, Object body) {
        super(message);
        this.body = body;
    }

    public InterruptException(String message) {
        super(message);
        this.body = null;
    }

    public InterruptException(Object body) {
        super();
        this.body = body;
    }

    public InterruptException() {
        super();
        this.body = null;
    }

    public Object getBody() {
        return body;
    }
}
