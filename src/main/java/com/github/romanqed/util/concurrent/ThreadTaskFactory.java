package com.github.romanqed.util.concurrent;

import com.github.romanqed.util.Checks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTaskFactory implements TaskFactory {
    protected ExecutorService executor;

    public ThreadTaskFactory(ExecutorService executor) {
        this.executor = Checks.requireNonNullElse(executor, Executors.newCachedThreadPool());
    }

    public ThreadTaskFactory() {
        this(null);
    }

    @Override
    public <T> Task<T> createTask(Callable<T> action) {
        return new Task<T>() {
            @Override
            public T call() throws Exception {
                return action.call();
            }

            @Override
            public ExecutorService getExecutor() {
                return executor;
            }
        };
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public boolean hasExecutor() {
        return executor != null;
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
