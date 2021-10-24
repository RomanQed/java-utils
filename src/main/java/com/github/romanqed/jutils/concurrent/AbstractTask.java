package com.github.romanqed.jutils.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class AbstractTask<T> implements Task<T> {
    @Override
    public CompletableFuture<T> async(Consumer<Exception> failure) {
        return CompletableFuture.supplyAsync(() -> checked(failure));
    }

    @Override
    public CompletableFuture<T> async() {
        return async(null);
    }

    @Override
    public Future<T> start(Consumer<T> success) {
        return start(success, null);
    }

    @Override
    public Future<T> start() {
        return start(null, null);
    }

    @Override
    public T checked(Consumer<Exception> failure) {
        try {
            return call();
        } catch (Exception e) {
            if (failure != null) {
                failure.accept(e);
            }
        }
        return null;
    }

    @Override
    public T silent() {
        return checked(null);
    }

    @Override
    public Future<T> start(Consumer<T> success, Consumer<Exception> failure) {
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
}
