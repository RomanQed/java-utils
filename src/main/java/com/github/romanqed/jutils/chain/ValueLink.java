package com.github.romanqed.jutils.chain;

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
