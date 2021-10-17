package com.github.romanqed.java8utils.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class AbstractTask<T> implements Task<T> {
    @Override
    public Future<T> async(Consumer<T> success, Consumer<Exception> failure) {
        ExecutorService executor = getExecutor();
        if (executor == null) {
            throw new IllegalStateException("The task was not added to the queue");
        }
        return executor.submit(() -> {
            try {
                T ret = call();
                if (success != null) {
                    success.accept(ret);
                }
                return ret;
            } catch (Exception e) {
                if (failure != null) {
                    failure.accept(e);
                } else {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }
}
