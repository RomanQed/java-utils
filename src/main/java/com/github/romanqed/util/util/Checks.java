package com.github.romanqed.util.util;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class Checks {
    public static final Predicate<String> NOT_EMPTY_STRING = e -> e != null && !e.isEmpty();
    public static final Predicate<Integer> GT_ZERO = e -> e != null && e > 0;
    public static final Predicate<Integer> LT_ZERO = e -> e != null && e < 0;

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

    public static <T> T requireNonNullElse(T object, T def) {
        if (object == null) {
            return Objects.requireNonNull(def);
        }
        return object;
    }

    public static <T> T requireCorrectValue(T object, Predicate<T> predicate) {
        if (!predicate.test(object)) {
            throw new IllegalArgumentException("Incorrect value!");
        }
        return object;
    }

    public static <T> T requireCorrectValueElse(T object, Predicate<T> predicate, T def) {
        if (!predicate.test(object)) {
            return requireCorrectValue(def, predicate);
        }
        return object;
    }

    public static String requireNonEmptyString(String object) {
        return requireCorrectValue(object, (string) -> Objects.nonNull(object) && !string.isEmpty());
    }

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
