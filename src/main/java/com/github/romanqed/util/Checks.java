package com.github.romanqed.util;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A set of different methods for verifying the correctness of data.
 */
public final class Checks {
    public static final Predicate<String> NOT_EMPTY_STRING = e -> e != null && !e.isEmpty();
    public static final Predicate<Integer> GT_ZERO = e -> e != null && e > 0;
    public static final Predicate<Integer> LT_ZERO = e -> e != null && e < 0;

    /**
     * Safely executes a {@link Callable}.
     *
     * @param callable lambda for execution
     * @param def      lambda, which will be called if an error occurred while executing the callable
     * @param <T>      return value type
     * @return result of callable executing
     * @throws IllegalStateException if an exception occurred during execution.
     */
    public static <T> T safetyCall(Callable<T> callable, Supplier<T> def) {
        try {
            return callable.call();
        } catch (Exception e) {
            return def.get();
        }
    }

    /**
     * Safely executes a {@link Callable}.
     *
     * @param callable lambda for execution
     * @param failure  lambda, which will be called if an error occurred while executing the callable
     * @param <T>      return value type
     * @return value returned from callable
     */
    public static <T> T safetyCall(Callable<T> callable, Consumer<Exception> failure) {
        try {
            return callable.call();
        } catch (Exception e) {
            failure.accept(e);
            return null;
        }
    }

    /**
     * Safely executes a {@link Callable}.
     *
     * @param callable lambda for execution
     * @param <T>      return value type
     * @return value returned from callable
     */
    public static <T> T safetyCall(Callable<T> callable) {
        return safetyCall(callable, (Exception e) -> {
            throw new IllegalStateException(e);
        });
    }

    /**
     * Safely executes a {@link Runnable}.
     *
     * @param runnable lambda for execution
     * @param failure  lambda, which will be called if an error occurred while executing the callable
     */
    public static void safetyRun(Runnable runnable, Consumer<Exception> failure) {
        try {
            runnable.run();
        } catch (Exception e) {
            failure.accept(e);
        }
    }

    /**
     * Safely executes a {@link Runnable}.
     *
     * @param runnable lambda for execution
     */
    public static void safetyRun(Runnable runnable) {
        safetyRun(runnable, (Exception e) -> {
            throw new IllegalStateException(e);
        });
    }

    /**
     * Requires a value not equal to null, otherwise it will return an alternative value.
     *
     * @param object object to check
     * @param def    alternative value
     * @param <T>    type of object
     * @return checked object
     */
    public static <T> T requireNonNullElse(T object, T def) {
        if (object == null) {
            return Objects.requireNonNull(def);
        }
        return object;
    }

    /**
     * Requires a value corresponding to the condition.
     *
     * @param object    object to check
     * @param predicate predicate that checks the condition
     * @param <T>       type of object
     * @return checked object
     */
    public static <T> T requireCorrectValue(T object, Predicate<T> predicate) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException("Incorrect value!");
        }
        return object;
    }

    /**
     * Requires a value corresponding to the condition, otherwise it will return an alternative value.
     *
     * @param object    object to check
     * @param predicate predicate that checks the condition
     * @param def       alternative value
     * @param <T>       type of object
     * @return checked object
     */
    public static <T> T requireCorrectValueElse(T object, Predicate<T> predicate, T def) {
        if (!predicate.test(object)) {
            return requireCorrectValue(def, predicate);
        }
        return object;
    }

    /**
     * Checks the string for non-emptiness.
     *
     * @param object string to check
     * @return checked string
     */
    public static String requireNonEmptyString(String object) {
        return requireCorrectValue(object, (string) -> object != null && !string.isEmpty());
    }
}
