package com.github.romanqed.jutils.structs.pipeline;

public class PipelineInterruptException extends RuntimeException {
    private final Object body;

    public PipelineInterruptException(String s, Object body) {
        super(s);
        this.body = body;
    }

    public PipelineInterruptException(Object body) {
        super();
        this.body = body;
    }

    public Object getBody() {
        return body;
    }
}
