package com.github.romanqed.java8utils.concurrent;

import com.github.romanqed.java8utils.util.Checks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseTaskFabric implements TaskFabric {
    protected ExecutorService executor;

    public BaseTaskFabric(ExecutorService executor) {
        this.executor = Checks.requireNonNullElse(executor, Executors.newCachedThreadPool());
    }

    public BaseTaskFabric() {
        this(null);
    }

    @Override
    public <T> Task<T> createTask(Callable<T> action) {
        return new AbstractTask<T>() {
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
    public boolean isQueue() {
        return executor != null;
    }
}
