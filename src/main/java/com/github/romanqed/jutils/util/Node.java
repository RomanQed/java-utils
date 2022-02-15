package com.github.romanqed.jutils.util;

import java.util.Objects;

public class Node<T, V> {
    private final T key;
    private final V value;

    public Node(T name, V value) {
        this.key = Objects.requireNonNull(name);
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }
}
