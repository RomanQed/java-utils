package com.github.romanqed.jutils.structs;

import java.util.Objects;

public class BaseLink implements Link {
    private Link tail;

    @Override
    public void attach(Link tail) {
        this.tail = Objects.requireNonNull(tail);
    }

    @Override
    public Link detach() {
        Link ret = tail;
        tail = null;
        return ret;
    }

    @Override
    public Link tail() {
        return tail;
    }

    @Override
    public String toString() {
        return "{this(" + hashCode() + ")} -> " + "{" + (tail() == null ? null : tail().hashCode()) + "}";
    }
}
