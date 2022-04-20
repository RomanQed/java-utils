package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.lambdas.Action;
import com.github.romanqed.jutils.util.Node;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class LinkedPipeline<T> implements Pipeline<T> {
    private final Object lock;
    private final Map<T, ActionLink<T>> body;
    private final Map<T, T> parents;
    private ActionLink<T> head;
    private ActionLink<T> tail;

    public LinkedPipeline() {
        lock = new Object();
        body = new ConcurrentHashMap<>();
        parents = new ConcurrentHashMap<>();
    }

    @Override
    public Object execute(Object o) throws Throwable {
        Object data = o;
        ActionLink<?> cur = head;
        while (cur != null) {
            try {
                data = cur.getBody().execute(data);
            } catch (InterruptException e) {
                return e.getBody();
            }
            cur = cur.tail();
        }
        return data;
    }

    @Override
    public CompletableFuture<Object> async(Object o) {
        ActionLink<?> cur = head;
        if (cur == null) {
            return CompletableFuture.completedFuture(o);
        }
        CompletableFuture<Object> ret = cur.getBody().async(o);
        cur = cur.tail();
        while (cur != null) {
            ret = ret.thenApplyAsync(Utils.packToFunction(cur.getBody()));
            cur = cur.tail();
        }
        return ret.exceptionally(Utils.EXCEPTION_HANDLER);
    }

    @Override
    public Action<?, ?> get(T key) {
        ActionLink<T> ret = body.get(key);
        if (ret != null) {
            return ret.getBody();
        }
        return null;
    }

    @Override
    public Action<?, ?> put(T key, Action<?, ?> value) {
        synchronized (lock) {
            ActionLink<T> toAdd = new ActionLink<>(key, value);
            ActionLink<T> ret = body.put(key, toAdd);
            if (ret == null) {
                if (head == null) {
                    head = toAdd;
                } else {
                    tail.attach(toAdd);
                    parents.put(key, tail.getKey());
                }
                tail = toAdd;
                return null;
            }
            if (ret == head) {
                head = toAdd;
                toAdd.attach(ret.detach());
            } else if (ret == tail) {
                tail = toAdd;
                body.get(parents.get(ret.getKey())).attach(toAdd);
            }
            return ret.getBody();
        }
    }

    @Override
    public Action<?, ?> remove(T key) {
        synchronized (lock) {
            ActionLink<T> ret = body.remove(key);
            if (ret == null) {
                return null;
            }
            ActionLink<T> tail = ret.detach();
            if (ret == head) {
                head = tail;
                if (tail == null) {
                    this.tail = null;
                } else {
                    parents.remove(tail.getKey());
                }
            } else {
                ActionLink<T> parent = body.get(parents.remove(key));
                if (ret == this.tail) {
                    parent.detach();
                    this.tail = parent;
                } else {
                    parents.put(tail.getKey(), parent.getKey());
                    parent.attach(tail);
                }
            }
            return ret.getBody();
        }
    }

    @Override
    public boolean contains(T key) {
        return body.containsKey(key);
    }

    private void insert(T key, ActionLink<T> value, boolean after) {
        Objects.requireNonNull(key);
        if (value == null) {
            return;
        }
        ActionLink<T> pos = body.get(key);
        Objects.requireNonNull(pos);
        if (after) {
            ActionLink<T> child = pos.detach();
            value.attach(child);
            pos.attach(value);
            if (pos == tail) {
                tail = value;
            } else {
                parents.put(child.getKey(), value.getKey());
            }
            parents.put(value.getKey(), key);
        } else {
            value.attach(pos);
            if (pos == head) {
                head = value;
            } else {
                ActionLink<T> parent = body.get(parents.remove(key));
                parent.attach(value);
                parents.put(value.getKey(), parent.getKey());
            }
            parents.put(key, value.getKey());
        }
    }

    private void insert(T key, T insertKey, Action<?, ?> value, boolean after) {
        if (body.containsKey(insertKey)) {
            throw new IllegalStateException("Pipeline already contains key " + insertKey + "!");
        }
        synchronized (lock) {
            ActionLink<T> toInsert = new ActionLink<>(insertKey, value);
            body.put(insertKey, toInsert);
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
    public void insertFirst(T key, Action<?, ?> value) {
        if (isEmpty()) {
            put(key, value);
            return;
        }
        insertBefore(head.getKey(), key, value);
    }

    @Override
    public void clear() {
        synchronized (lock) {
            tail = null;
            head = null;
            parents.clear();
            body.clear();
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
        return new LinkedPipelineIterator(head);
    }

    @Override
    public String toString() {
        return body.toString();
    }

    private class LinkedPipelineIterator implements Iterator<Node<T, Action<Object, Object>>> {
        private ActionLink<T> head;

        private LinkedPipelineIterator(ActionLink<T> head) {
            this.head = head;
        }

        @Override
        public boolean hasNext() {
            return head != null;
        }

        @Override
        public Node<T, Action<Object, Object>> next() {
            if (head == null) {
                throw new NoSuchElementException();
            }
            ActionLink<T> ret = head;
            head = head.tail();
            return new Node<>(ret.getKey(), ret.getBody());
        }
    }
}
