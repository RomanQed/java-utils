package com.github.romanqed.util.concurrent;

import com.github.romanqed.util.util.Checks;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An interface describing the general appearance of a universal task.
 *
 * @param <T> type of return value
 */
public interface Task<T> extends Callable<T> {
    /**
     * Runs the task by {@link ExecutorService}, if the execution is successful,
     * success will be executed, if not, failure.
     *
     * @param success the consumer who will be called in case of success, accepts the result of the execution
     * @param failure the consumer executing in case of an exception, accepts the thrown exception
     * @return the {@link Future} bound to {@link ExecutorService}
     */
    default Future<T> start(Consumer<T> success, Consumer<Throwable> failure) {
        ExecutorService executor = getExecutor();
        if (executor == null) {
            throw new IllegalStateException("The task has no executor");
        }
        Consumer<T> toCall = Checks.requireNonNullElse(success, e -> {
        });
        if (failure != null) {
            return executor.submit(() -> {
                T ret = checked(failure);
                toCall.accept(ret);
                return ret;
            });
        }
        return executor.submit(() -> {
            T ret = call();
            toCall.accept(ret);
            return ret;
        });
    }

    /**
     * Runs the task by {@link ExecutorService}, if the execution is successful, success will be executed.
     *
     * @param success the consumer who will be called in case of success, accepts the result of the execution
     * @return the {@link Future} bound to {@link ExecutorService}
     */
    default Future<T> start(Consumer<T> success) {
        return start(success, null);
    }

    /**
     * Runs the task by {@link ExecutorService}.
     *
     * @return the {@link Future} bound to {@link ExecutorService}
     */
    default Future<T> start() {
        return start(null, null);
    }

    /**
     * Runs task asynchronously.
     *
     * @return {@link CompletableFuture} instance bound to internal {@link ExecutorService}
     */
    default CompletableFuture<T> async() {
        ExecutorService executor = getExecutor();
        Supplier<T> body = () -> {
            try {
                return call();
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
        if (executor == null) {
            return CompletableFuture.supplyAsync(body);
        }
        return CompletableFuture.supplyAsync(body, executor);
    }

    /**
     * Performs a task blocking in the caller's thread, handling exceptions.
     *
     * @param failure exception handler
     * @return task execution result
     */
    default T checked(Consumer<Throwable> failure) {
        try {
            return call();
        } catch (Throwable e) {
            failure.accept(e);
            return null;
        }
    }

    /**
     * Performs a task blocking in the caller's thread, silently handling exceptions.
     *
     * @return task execution result
     */
    default T silent() {
        try {
            return call();
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * @return {@link ExecutorService} instance bounded to this {@link Task}
     */
    ExecutorService getExecutor();
}
