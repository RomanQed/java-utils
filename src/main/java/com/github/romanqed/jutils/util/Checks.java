package com.github.romanqed.jutils.util;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public class Checks {
    public static <T> T requireNonException(Callable<T> expression, T def) {
        try {
            return expression.call();
        } catch (Exception e) {
            return def;
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
}
