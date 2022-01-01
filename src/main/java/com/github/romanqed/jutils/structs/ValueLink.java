package com.github.romanqed.jutils.structs;

public class ValueLink<E> extends AbstractLink<ValueLink<E>> {
    private E value;

    public ValueLink(E initValue) {
        this.value = initValue;
    }

    public ValueLink() {
        this(null);
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }
}
