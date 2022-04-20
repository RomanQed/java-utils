package com.github.romanqed.jutils.lambdas;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;

public class MetaLambdas {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final LambdaClass<?> HANDLER = LambdaClass.fromClass(Handler.class);
    private static final LambdaClass<?> ACTION = LambdaClass.fromClass(Action.class);
    private static final LambdaClass<?> CALLABLE = LambdaClass.fromClass(Callable.class);

    public static MethodType extractType(Method method) throws IllegalAccessException {
        Objects.requireNonNull(method);
        MethodHandle handle = LOOKUP.unreflect(method);
        return handle.type();
    }

    public static MethodType extractDynamicType(Method method) throws IllegalAccessException {
        return extractType(method).dropParameterTypes(0, 1);
    }

    public static Action<Object[], Object> packAnyMethod(Method method, Object bind, int arguments) throws Throwable {
        Objects.requireNonNull(method);
        method.setAccessible(true);
        MethodHandle handle = LOOKUP.unreflect(method).asSpreader(Object[].class, arguments);
        if (bind != null) {
            handle = handle.bindTo(bind);
        }
        return handle::invoke;
    }

    public static Action<Object[], Object> packAnyMethod(Method method, int arguments) throws Throwable {
        return packAnyMethod(method, null, arguments);
    }

    @SuppressWarnings("unchecked")
    private static <T> T packLambdaHandle(LambdaClass<T> clazz, MethodHandle handle, Object bind) throws Throwable {
        Objects.requireNonNull(clazz);
        Method lambdaMethod = clazz.getLambdaMethod();
        MethodType lambdaType = extractDynamicType(lambdaMethod);
        MethodType bindType = MethodType.methodType(clazz.getLambdaClass());
        MethodType sourceType = handle.type();
        if (bind != null) {
            bindType = bindType.appendParameterTypes(bind.getClass());
            sourceType = sourceType.dropParameterTypes(0, 1);
        }
        CallSite callSite = LambdaMetafactory.metafactory(
                LOOKUP,
                lambdaMethod.getName(),
                bindType,
                lambdaType,
                handle,
                sourceType
        );
        MethodHandle ret = bind == null ? callSite.getTarget() : callSite.getTarget().bindTo(bind);
        return (T) ret.invoke();
    }

    public static <T> T packLambdaMethod(LambdaClass<T> clazz, Method method, Object bind) throws Throwable {
        Objects.requireNonNull(method);
        MethodHandle handle = LOOKUP.unreflect(method);
        return packLambdaHandle(clazz, handle, bind);
    }

    public static <T> T packLambdaConstructor(LambdaClass<T> clazz, Constructor<?> constructor) throws Throwable {
        Objects.requireNonNull(constructor);
        MethodHandle handle = LOOKUP.unreflectConstructor(constructor);
        return packLambdaHandle(clazz, handle, null);
    }

    public static <T> T packLambdaMethod(LambdaClass<T> clazz, Method method) throws Throwable {
        return packLambdaMethod(clazz, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Handler<T> packHandler(Method method, Object bind) throws Throwable {
        return (Handler<T>) packLambdaMethod(HANDLER, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <T> Handler<T> packHandler(Method method) throws Throwable {
        return (Handler<T>) packLambdaMethod(HANDLER, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Action<T, R> packAction(Method method, Object bind) throws Throwable {
        return (Action<T, R>) packLambdaMethod(ACTION, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Action<T, R> packAction(Method method) throws Throwable {
        return (Action<T, R>) packLambdaMethod(ACTION, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Action<T, R> packConstructor(Class<R> clazz, Class<T> argumentClazz) throws Throwable {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(argumentClazz);
        Constructor<R> toPack = clazz.getDeclaredConstructor(argumentClazz);
        MethodHandle handle = LOOKUP.unreflectConstructor(toPack);
        return (Action<T, R>) packLambdaHandle(ACTION, handle, null);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packCallable(Method method, Object bind) throws Throwable {
        return (Callable<R>) packLambdaMethod(CALLABLE, method, bind);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packCallable(Method method) throws Throwable {
        return (Callable<R>) packLambdaMethod(CALLABLE, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <R> Callable<R> packConstructor(Class<R> clazz) throws Throwable {
        Objects.requireNonNull(clazz);
        Constructor<R> toPack = clazz.getDeclaredConstructor();
        MethodHandle handle = LOOKUP.unreflectConstructor(toPack);
        return (Callable<R>) packLambdaHandle(CALLABLE, handle, null);
    }
}
