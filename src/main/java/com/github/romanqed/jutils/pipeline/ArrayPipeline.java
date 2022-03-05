package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.Action;
import com.github.romanqed.jutils.util.Node;

import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    public CompletableFuture<Object> async(Object o) {
        Iterator<Node<T, Action<Object, Object>>> iterator = body.iterator();
        if (!iterator.hasNext()) {
            return CompletableFuture.completedFuture(o);
        }
        CompletableFuture<Object> ret = iterator.next().getValue().async(o);
        while (iterator.hasNext()) {
            ret = ret.thenApplyAsync(Utils.packToFunction(iterator.next().getValue()));
        }
        return ret.exceptionally(Utils.EXCEPTION_HANDLER);
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
            Integer index = indexes.remove(key);
            if (index == null) {
                return null;
            }
            for (Map.Entry<T, Integer> entry : indexes.entrySet()) {
                int entryValue = entry.getValue();
                if (entryValue > index) {
                    entry.setValue(entryValue - 1);
                }
            }
            return body.remove((int) index).getValue();
        }
    }

    @Override
    public boolean contains(T key) {
        return indexes.containsKey(key);
    }

    private void insert(T key, Node<T, Action<Object, Object>> value, boolean after) {
        Integer index = indexes.get(key);
        if (index == null) {
            throw new NoSuchElementException();
        }
        int match = index;
        if (after) {
            index += 1;
        } else {
            match -= 1;
        }
        for (Map.Entry<T, Integer> entry : indexes.entrySet()) {
            int entryValue = entry.getValue();
            if (entryValue > match) {
                entry.setValue(entryValue + 1);
            }
        }
        indexes.put(value.getKey(), index);
        body.add(index, value);
    }

    @SuppressWarnings("unchecked")
    private void insert(T key, T insertKey, Action<?, ?> value, boolean after) {
        if (indexes.containsKey(insertKey)) {
            throw new IllegalStateException("Pipeline already contains key " + insertKey + "!");
        }
        synchronized (lock) {
            Node<T, Action<Object, Object>> toInsert = new Node<>(insertKey, (Action<Object, Object>) value);
            insert(key, toInsert, after);
        }
    }

    @Override
    public void insertAfter(T key, T insertKey, Action<?, ?> value) {
        insert(key, insertKey, value, true);
    }

    @Override
    public void insertBefore(T key, T insertKey, Action<?, ?> value) {
        insert(key, insertKey, value, false);
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
    public String toString() {
        return body.toString();
    }

    @Override
    public Iterator<Node<T, Action<Object, Object>>> iterator() {
        return body.iterator();
    }
}
