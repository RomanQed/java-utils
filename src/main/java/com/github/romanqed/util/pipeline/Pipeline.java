package com.github.romanqed.util.pipeline;

import com.github.romanqed.util.Action;
import com.github.romanqed.util.Node;

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
     * Puts a value with a key at the end of the chain of actions.
     *
     * @param key   action key to be inserted
     * @param value action to be inserted
     * @return an action that has already been with the same key, or null
     */
    Action<?, ?> put(T key, Action<?, ?> value);

    /**
     * Removes the action by key.
     *
     * @param key the key by which the search and deletion will be performed.
     * @return removed action, if it existed, or null
     */
    Action<?, ?> remove(T key);

    /**
     * Checks whether the pipeline contains a key.
     *
     * @param key the key to be checked
     * @return result of checking
     */
    boolean contains(T key);

    /**
     * Inserts an action with an insert key after an action with a key equal to the one received.
     *
     * @param key       the key after which the insert will be
     * @param insertKey the key to insert with
     * @param value     the action to insert
     */
    void insertAfter(T key, T insertKey, Action<?, ?> value);

    /**
     * Inserts an action with an insert key before an action with a key equal to the one received.
     *
     * @param key       the key before which the insert will be
     * @param insertKey the key to insert with
     * @param value     the action to insert
     */
    void insertBefore(T key, T insertKey, Action<?, ?> value);

    /**
     * Inserts an action at the beginning of the pipeline
     *
     * @param key   the key to insert with
     * @param value the action to insert
     */
    void insertFirst(T key, Action<?, ?> value);

    /**
     * Clears pipeline.
     */
    void clear();

    /**
     * Returns size of pipeline.
     *
     * @return number of actions in the pipeline
     */
    int size();

    /**
     * Checks if pipeline is empty.
     *
     * @return boolean flag
     */
    boolean isEmpty();

    /**
     * Puts all actions from received pipeline at the end of the chain of actions.
     *
     * @param value pipeline to be put
     */
    default void putAll(Pipeline<T> value) {
        Objects.requireNonNull(value);
        for (Node<T, Action<Object, Object>> node : value) {
            put(node.getKey(), node.getValue());
        }
    }

    /**
     * Inserts all actions from received pipeline with after an action with a key equal to the one received.
     *
     * @param key   the key after which the insert will be
     * @param value pipeline to be inserted
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
     * Inserts all actions from received pipeline with before an action with a key equal to the one received.
     *
     * @param key   the key before which the insert will be
     * @param value pipeline to be inserted
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

    /**
     * Inserts all actions from received pipeline at the beginning of the pipeline
     *
     * @param value pipeline to be inserted
     */
    default void insertFirst(Pipeline<T> value) {
        Objects.requireNonNull(value);
        Iterator<Node<T, Action<Object, Object>>> iterator = value.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        Node<T, Action<Object, Object>> first = iterator.next();
        insertFirst(first.getKey(), first.getValue());
        while (iterator.hasNext()) {
            Node<T, Action<Object, Object>> next = iterator.next();
            insertAfter(first.getKey(), next.getKey(), next.getValue());
            first = next;
        }
    }
}
