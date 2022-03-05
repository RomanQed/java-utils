package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.Action;
import com.github.romanqed.jutils.util.Node;

import java.util.Iterator;
import java.util.Objects;

/**
 * <p>An interface describing a structure capable of storing and editing the order of sequentially executed actions.</p>
 * <p>Execution may be interrupted by throwing an {@link InterruptException}.</p>
 * <p>Also, if any other exception is thrown inside during execution, it will throw it outside.</p>
 * <p>Since the pipeline is the heir of the action, nested pipelines can be created.</p>
 * <p>Access and editing of all actions is performed by the key.</p>
 * <p>It has built-in support for asynchronous execution.</p>
 *
 * @param <T> The type of value to be used as the key.
 */
public interface Pipeline<T> extends Action<Object, Object>, Iterable<Node<T, Action<Object, Object>>> {
    /**
     * Returns the action belonging to the passed key.
     *
     * @param key action key, cannot be null.
     * @return found action or null
     */
    Action<?, ?> get(T key);

    /**
     * @param key
     * @param value
     * @return
     */
    Action<?, ?> put(T key, Action<?, ?> value);

    /**
     * @param key
     * @return
     */
    Action<?, ?> remove(T key);

    /**
     * @param key
     * @return
     */
    boolean contains(T key);

    /**
     * @param key
     * @param insertKey
     * @param value
     */
    void insertAfter(T key, T insertKey, Action<?, ?> value);

    /**
     * @param key
     * @param insertKey
     * @param value
     */
    void insertBefore(T key, T insertKey, Action<?, ?> value);

    /**
     *
     */
    void clear();

    /**
     * @return
     */
    int size();

    /**
     * @return
     */
    boolean isEmpty();

    /**
     * @param value
     */
    default void putAll(Pipeline<T> value) {
        Objects.requireNonNull(value);
        for (Node<T, Action<Object, Object>> node : value) {
            put(node.getKey(), node.getValue());
        }
    }

    /**
     * @param key
     * @param value
     */
    default void insertAfter(T key, Pipeline<T> value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        for (Node<T, Action<Object, Object>> node : value) {
            insertAfter(key, node.getKey(), node.getValue());
            key = node.getKey();
        }
    }

    /**
     * @param key
     * @param value
     */
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
