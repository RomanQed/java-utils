package com.github.romanqed.jutils.structs;

public interface Linkable<E> extends Iterable<E> {
    void attach(Linkable<E> tail);

    Linkable<E> detach();

    Linkable<E> tail();

    E getValue();

    void setValue(E value);
}
