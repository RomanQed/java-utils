package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.Action;
import com.github.romanqed.jutils.util.Node;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArrayPipeline<T> implements Pipeline<T> {
    private final Object lock;
    private final List<Node<T, Action<Object, Object>>> body;
    private final Map<T, Integer> indexes;

    public ArrayPipeline() {
        lock = new Object();
        body = new ArrayList<>();
        indexes = new ConcurrentHashMap<>();
    }

    @Override
    public Object execute(Object o) throws Exception {
        Object data = o;
        for (Node<T, Action<Object, Object>> node : body) {
            try {
                data = node.getValue().execute(data);
            } catch (InterruptException e) {
                return e.getBody();
            }
        }
        return data;
    }

    @Override
    public Action<?, ?> get(T key) {
        Objects.requireNonNull(key);
        Integer index = indexes.get(key);
        if (index == null) {
            return null;
        }
        return body.get(index).getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Action<?, ?> put(T key, Action<?, ?> value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        synchronized (lock) {
            Integer index = indexes.get(key);
            Node<T, Action<Object, Object>> toAdd = new Node<>(key, (Action<Object, Object>) value);
            if (index != null) {
                Action<?, ?> ret = body.get(index).getValue();
                body.set(index, toAdd);
                return ret;
            }
            body.add(toAdd);
            indexes.put(key, body.size() - 1);
            return null;
        }
    }

    @Override
    public Action<?, ?> remove(T key) {
        Objects.requireNonNull(key);
        synchronized (lock) {
            Integer index = indexes.get(key);
            if (index == null) {
                return null;
            }
            return body.remove((int) index).getValue();
        }
    }

    private void insert(T key, Node<T, Action<Object, Object>> value, boolean after) {

    }

    @Override
    public void insertAfter(T key, T insertKey, Action<?, ?> value) {

    }

    @Override
    public void insertBefore(T key, T insertKey, Action<?, ?> value) {

    }

    @Override
    public void clear() {
        synchronized (lock) {
            body.clear();
            indexes.clear();
        }
    }

    @Override
    public int size() {
        return body.size();
    }

    @Override
    public boolean isEmpty() {
        return body.isEmpty();
    }

    @Override
    public Iterator<Node<T, Action<Object, Object>>> iterator() {
        return body.iterator();
    }
}
