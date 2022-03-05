package com.github.romanqed.jutils.util;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface Handler<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws Exception all exceptions that may occur in the process
     */
    void handle(T t) throws Exception;

    /**
     * Returns completable future contains this handler executing.
     *
     * @param t the input argument
     * @return {@link CompletableFuture} returns {@link Void}
     */
    default CompletableFuture<Void> async(T t) {
        return CompletableFuture.runAsync(() -> {
            try {
                handle(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default Handler<T> andThen(Handler<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            handle(t);
            after.handle(t);
        };
    }
}
