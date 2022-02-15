package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.AbstractLink;
import com.github.romanqed.jutils.util.Action;

import java.util.Objects;

public class ActionLink<T> extends AbstractLink<ActionLink<T>> {
    private final Action<Object, Object> body;
    private final T key;

    @SuppressWarnings("unchecked")
    public ActionLink(T key, Action<?, ?> body) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(body);
        this.key = key;
        this.body = (Action<Object, Object>) body;
    }

    public T getKey() {
        return key;
    }

    public Action<Object, Object> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "this(" + key + ") -> " + (tail() == null ? null : tail().key);
    }
}
