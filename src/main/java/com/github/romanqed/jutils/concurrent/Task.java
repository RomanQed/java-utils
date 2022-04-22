package com.github.romanqed.jutils.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface Task<T> extends Callable<T> {
    default Future<T> start(Consumer<T> success, Consumer<Exception> failure) {
        ExecutorService executor = getExecutor();
        if (executor == null) {
            throw new IllegalStateException("The task has no executor");
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

    default Future<T> start(Consumer<T> success) {
        return start(success, null);
    }

    default Future<T> start() {
        return start(null, null);
    }

    default CompletableFuture<T> async(Consumer<Exception> failure) {
        ExecutorService executor = getExecutor();
        if (executor == null) {
            return CompletableFuture.supplyAsync(() -> checked(failure));
        }
        return CompletableFuture.supplyAsync(() -> checked(failure), executor);
    }

    default CompletableFuture<T> async() {
        return async(null);
    }

    default T checked(Consumer<Exception> failure) {
        try {
            return call();
        } catch (Exception e) {
            if (failure != null) {
                failure.accept(e);
            }
        }
        return null;
    }

    default T silent() {
        return checked(null);
    }

    ExecutorService getExecutor();
}
