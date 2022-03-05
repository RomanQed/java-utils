package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.Action;

import java.util.function.Function;

class Utils {
    static final Function<Throwable, Object> EXCEPTION_HANDLER;

    static {
        EXCEPTION_HANDLER = throwable -> {
            throwable = throwable.getCause();
            if (throwable instanceof InterruptException) {
                return ((InterruptException) throwable).getBody();
            }
            throw new RuntimeException(throwable);
        };
    }

    static Function<Object, Object> packToFunction(Action<Object, Object> action) {
        return value -> {
            try {
                if (action instanceof Pipeline) {
                    return action.async(value).get();
                }
                return action.execute(value);
            } catch (InterruptException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
