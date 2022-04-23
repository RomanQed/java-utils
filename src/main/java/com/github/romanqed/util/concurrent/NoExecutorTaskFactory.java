package com.github.romanqed.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class NoExecutorTaskFactory implements TaskFactory {
    @Override
    public <T> Task<T> createTask(Callable<T> action) {
        return new Task<T>() {
            @Override
            public ExecutorService getExecutor() {
                return null;
            }

            @Override
            public T call() throws Exception {
                return action.call();
            }
        };
    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }

    @Override
    public boolean hasExecutor() {
        return false;
    }

    @Override
    public void close() {
        throw new IllegalStateException("The task fabric does not contain an executor");
    }
}
