package com.github.romanqed.jutils.util;

public interface Chain<E extends Link> extends Iterable<E> {
    void add(E link);

    E remove();

    E next();

    void clear();
}
