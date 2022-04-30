package com.github.romanqed.util;

/**
 * A class describing a key-value structure with key-only hashing.
 *
 * @param <K> type of key object
 * @param <V> type of value object
 */
public class Record<K, V> extends Pair<K, V> {
    public Record(K key, V value) {
        super(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Record)) {
            return false;
        }
        return key.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
