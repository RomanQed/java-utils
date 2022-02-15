package com.github.romanqed.jutils.pipeline;

public class InterruptException extends RuntimeException {
    private final Object body;

    public InterruptException(String s, Object body) {
        super(s);
        this.body = body;
    }

    public InterruptException(String s) {
        super(s);
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
