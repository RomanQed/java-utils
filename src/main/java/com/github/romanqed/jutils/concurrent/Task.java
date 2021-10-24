package com.github.romanqed.jutils.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface Task<T> extends Callable<T> {
    Future<T> start(Consumer<T> success, Consumer<Exception> failure);

    Future<T> start(Consumer<T> success);

    Future<T> start();

    CompletableFuture<T> async(Consumer<Exception> failure);

    CompletableFuture<T> async();

    T checked(Consumer<Exception> failure);

    T silent();

    ExecutorService getExecutor();
}
