package com.github.romanqed.jutils.chain;

public interface Chain<E extends Link> extends Iterable<E> {
    void add(E link);

    E remove();

    E next();

    void clear();
}
