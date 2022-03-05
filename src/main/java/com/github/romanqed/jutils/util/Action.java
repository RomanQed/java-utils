package com.github.romanqed.jutils.util;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface Action<T, R> {
    /**
     * Returns an action that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the action
     * @return an action that always returns its input argument
     */
    static <T> Action<T, T> identity() {
        return t -> t;
    }

    /**
     * Execute this action for the given argument.
     *
     * @param t the action argument
     * @return the action result
     * @throws Exception any exception that can be thrown in the action process
     */
    R execute(T t) throws Exception;

    /**
     * Returns completable future contains this action executing.
     *
     * @param t the action argument
     * @return {@link CompletableFuture} returns action result
     */
    default CompletableFuture<R> async(T t) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Returns a composed action that first executes the {@code before}
     * action for its input, and then executes this action for the result.
     *
     * @param <V>    the type of input to the {@code before} action, and to the
     *               composed action
     * @param before the action to execute before this action is executed
     * @return a composed action that first executes the {@code before}
     * action and then executes this action
     * @throws NullPointerException if before is null
     */
    default <V> Action<V, R> compose(Action<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> execute(before.execute(v));
    }

    /**
     * Returns a composed action that first executes this action to
     * its input, and then executes the {@code after} action to the result.
     *
     * @param <V>   the type of output of the {@code after} action, and of the
     *              composed action
     * @param after the action to apply after this action is applied
     * @return a composed action that first executes this action and then
     * executes the {@code after} action
     * @throws NullPointerException if after is null
     */
    default <V> Action<T, V> andThen(Action<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.execute(execute(t));
    }
}
