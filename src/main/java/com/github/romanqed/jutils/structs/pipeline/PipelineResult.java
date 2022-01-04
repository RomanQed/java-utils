package com.github.romanqed.jutils.structs.pipeline;

public class PipelineResult {
    private final boolean interrupted;
    private final Object result;
    private final Exception exception;

    protected PipelineResult(Object result, boolean interrupted, Exception exception) {
        this.result = result;
        this.interrupted = interrupted;
        this.exception = exception;
    }

    protected PipelineResult(Object result, boolean interrupted) {
        this(result, interrupted, null);
    }

    protected PipelineResult(Object result) {
        this(result, false, null);
    }

    public Object getResult() {
        return result;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public Exception getException() {
        return exception;
    }
}
