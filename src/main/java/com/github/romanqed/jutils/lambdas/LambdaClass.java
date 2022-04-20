package com.github.romanqed.jutils.lambdas;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class LambdaClass<T> {
    private final Class<T> clazz;
    private final Method lambdaMethod;

    private LambdaClass(Class<T> clazz, Method method) {
        this.clazz = clazz;
        this.lambdaMethod = method;
    }

    public static <T> LambdaClass<T> fromClass(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("Invalid lambda class");
        }
        Optional<Method> found = Arrays.
                stream(clazz.getMethods()).
                filter(e -> Modifier.isAbstract(e.getModifiers())).
                findFirst();
        if (!found.isPresent()) {
            throw new IllegalArgumentException("Invalid lambda class");
        }
        return new LambdaClass<>(clazz, found.get());
    }

    public Class<T> getLambdaClass() {
        return clazz;
    }

    public Method getLambdaMethod() {
        return lambdaMethod;
    }

    @Override
    public String toString() {
        return "Lambda " + clazz.getSimpleName() + " with method " + lambdaMethod.getName();
    }
}
