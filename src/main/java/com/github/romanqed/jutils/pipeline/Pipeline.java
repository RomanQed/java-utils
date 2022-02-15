package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.Action;
import com.github.romanqed.jutils.util.Node;

import java.util.Iterator;
import java.util.Objects;

public interface Pipeline<T> extends Action<Object, Object>, Iterable<Node<T, Action<Object, Object>>> {
    Action<?, ?> get(T key);

    Action<?, ?> put(T key, Action<?, ?> value);

    Action<?, ?> remove(T key);

    boolean contains(T key);

    void insertAfter(T key, T insertKey, Action<?, ?> value);

    void insertBefore(T key, T insertKey, Action<?, ?> value);

    void clear();

    int size();

    boolean isEmpty();

    default void putAll(Pipeline<T> value) {
        Objects.requireNonNull(value);
        for (Node<T, Action<Object, Object>> node : value) {
            put(node.getKey(), node.getValue());
        }
    }

    default void insertAfter(T key, Pipeline<T> value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        for (Node<T, Action<Object, Object>> node : value) {
            insertAfter(key, node.getKey(), node.getValue());
            key = node.getKey();
        }
    }

    default void insertBefore(T key, Pipeline<T> value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Iterator<Node<T, Action<Object, Object>>> iterator = value.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        Node<T, Action<Object, Object>> toAdd = iterator.next();
        insertBefore(key, toAdd.getKey(), toAdd.getValue());
        key = toAdd.getKey();
        while (iterator.hasNext()) {
            toAdd = iterator.next();
            insertAfter(key, toAdd.getKey(), toAdd.getValue());
            key = toAdd.getKey();
        }
    }
}
