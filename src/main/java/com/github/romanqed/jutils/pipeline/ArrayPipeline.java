package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.Action;
import com.github.romanqed.jutils.util.Node;

import java.util.Iterator;

public class ArrayPipeline<T> implements Pipeline<T> {
    @Override
    public Action<?, ?> get(T key) {
        return null;
    }

    @Override
    public Action<?, ?> put(T key, Action<?, ?> value) {
        return null;
    }

    @Override
    public Action<?, ?> remove(T key) {
        return null;
    }

    @Override
    public void insertAfter(T key, T insertKey, Action<?, ?> value) {

    }

    @Override
    public void insertBefore(T key, T insertKey, Action<?, ?> value) {

    }

    @Override
    public void clear() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Object execute(Object o) throws Exception {
        return null;
    }

    @Override
    public Iterator<Node<T, Action<Object, Object>>> iterator() {
        return null;
    }
}
