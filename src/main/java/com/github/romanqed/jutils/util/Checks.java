package com.github.romanqed.jutils.util;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Checks {
    /**
     * @param expression
     * @param def
     * @param <T>
     * @return
     */
    public static <T> T requireNonException(Callable<T> expression, Callable<T> def) {
        Objects.requireNonNull(expression);
        Objects.requireNonNull(def);
        try {
            return expression.call();
        } catch (Exception e) {
            try {
                return def.call();
            } catch (Exception defException) {
                throw new IllegalStateException("Exception when calling def!", defException);
            }
        }
    }

    /**
     * @param object
     * @param def
     * @param <T>
     * @return
     */
    public static <T> T requireNonNullElse(T object, T def) {
        if (object == null) {
            return Objects.requireNonNull(def);
        }
        return object;
    }

    /**
     * @param object
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> T requireCorrectValue(T object, Predicate<T> predicate) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException("Incorrect value!");
        }
        return object;
    }

    /**
     * @param object
     * @param predicate
     * @param def
     * @param <T>
     * @return
     */
    public static <T> T requireCorrectValueElse(T object, Predicate<T> predicate, T def) {
        if (!predicate.test(object)) {
            return requireCorrectValue(def, predicate);
        }
        return object;
    }

    /**
     * @param object
     * @return
     */
    public static String requireNonEmptyString(String object) {
        return requireCorrectValue(object, (string) -> Objects.nonNull(object) && !string.isEmpty());
    }

    /**
     * @param callable
     * @param failure
     * @param <T>
     * @return
     */
    public static <T> T safetyCall(Callable<T> callable, Consumer<Exception> failure) {
        try {
            return callable.call();
        } catch (Exception e) {
            if (failure != null) {
                failure.accept(e);
            }
            return null;
        }
    }

    /**
     * @param runnable
     * @param failure
     */
    public static void safetyRun(Runnable runnable, Consumer<Exception> failure) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (failure != null) {
                failure.accept(e);
            }
        }
    }
}
