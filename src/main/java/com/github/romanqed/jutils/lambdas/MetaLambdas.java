package com.github.romanqed.jutils.lambdas;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * A set of methods that simplify interaction with the meta-lambda generator added in java 8.
 */
public final class MetaLambdas {
    private static final MetaFactory FACTORY = new MetaFactory(MethodHandles.lookup());
    private static final LambdaClass<?> HANDLER = LambdaClass.fromClass(Handler.class);
    private static final LambdaClass<?> ACTION = LambdaClass.fromClass(Action.class);
    private static final LambdaClass<?> CALLABLE = LambdaClass.fromClass(Callable.class);

    @SuppressWarnings("unchecked")
    public static <T> Handler<T> packHandler(Method method, Object bind) throws Throwable {
        return (Handler<T>) FACTORY.packLambdaMethod(HANDLER, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <T> Handler<T> packHandler(Method method) throws Throwable {
        return (Handler<T>) FACTORY.packLambdaMethod(HANDLER, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Action<T, R> packAction(Method method, Object bind) throws Throwable {
        return (Action<T, R>) FACTORY.packLambdaMethod(ACTION, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Action<T, R> packAction(Method method) throws Throwable {
        return (Action<T, R>) FACTORY.packLambdaMethod(ACTION, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Action<T, R> packConstructor(Class<R> clazz, Class<T> argumentClazz) throws Throwable {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(argumentClazz);
        Constructor<R> toPack = clazz.getDeclaredConstructor(argumentClazz);
        MethodHandle handle = FACTORY.getLookup().unreflectConstructor(toPack);
        return (Action<T, R>) FACTORY.packLambdaHandle(ACTION, handle, null);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packCallable(Method method, Object bind) throws Throwable {
        return (Callable<R>) FACTORY.packLambdaMethod(CALLABLE, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packCallable(Method method) throws Throwable {
        return (Callable<R>) FACTORY.packLambdaMethod(CALLABLE, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packConstructor(Class<R> clazz) throws Throwable {
        Objects.requireNonNull(clazz);
        Constructor<R> toPack = clazz.getDeclaredConstructor();
        MethodHandle handle = FACTORY.getLookup().unreflectConstructor(toPack);
        return (Callable<R>) FACTORY.packLambdaHandle(CALLABLE, handle, null);
    }
}
