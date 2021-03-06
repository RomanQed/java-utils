package com.github.romanqed.util.pipeline;

import com.github.romanqed.util.Action;

import java.util.function.Function;

final class Util {
    static final Function<Throwable, Object> EXCEPTION_HANDLER;

    static {
        EXCEPTION_HANDLER = throwable -> {
            Throwable cause = throwable.getCause();
            if (cause instanceof InterruptException) {
                return ((InterruptException) cause).getBody();
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw (RuntimeException) cause;
        };
    }

    static Function<Object, Object> packToFunction(Action<Object, Object> action) {
        return value -> {
            try {
                if (action instanceof Pipeline) {
                    return action.async(value).get();
                }
                return action.execute(value);
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}
