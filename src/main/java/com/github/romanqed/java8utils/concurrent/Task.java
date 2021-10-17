package com.github.romanqed.java8utils.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface Task<T> extends Callable<T> {
    Future<T> async(Consumer<T> success, Consumer<Exception> failure);

    default Future<T> async(Consumer<T> success) {
        return async(success, null);
    }

    default Future<T> async() {
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
