package com.github.romanqed.jutils.structs;

import java.util.Objects;

public abstract class AbstractLink<T extends Link> implements Link {
    private T tail;

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Link> void attach(E tail) {
        this.tail = (T) Objects.requireNonNull(tail);
    }

    @Override
    public T detach() {
        T ret = tail;
        tail = null;
        return ret;
    }

    @Override
    public T tail() {
        return tail;
    }

    @Override
    public String toString() {
        return "{this(" + hashCode() + ")} -> " + "{" + (tail() == null ? null : tail().hashCode()) + "}";
    }
}
