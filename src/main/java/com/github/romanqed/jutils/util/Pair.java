package com.github.romanqed.jutils.util;

import java.util.Objects;

/**
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {
    protected final K key;
    protected final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return
     */
    public K getKey() {
        return key;
    }

    /**
     * @return
     */
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
        Pair<K, V> pair;
        try {
            pair = (Pair<K, V>) o;
        } catch (Exception e) {
            return false;
        }
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }
}