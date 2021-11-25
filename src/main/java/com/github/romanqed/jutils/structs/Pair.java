package com.github.romanqed.jutils.structs;

import com.github.romanqed.jutils.util.Checks;

import java.util.Objects;

public class Pair<K, V> {
    protected final K key;
    protected final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public int hashCode() {
        return Checks.requireNonNullElse(key.hashCode(), 0) * 13 +
                Checks.requireNonNullElse(value.hashCode(), 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<K, V> pair = (Pair<K, V>) o;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }
}