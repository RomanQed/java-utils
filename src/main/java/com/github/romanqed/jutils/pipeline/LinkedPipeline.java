package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.Action;
import com.github.romanqed.jutils.util.Node;

import java.util.*;

public class LinkedPipeline implements Pipeline<String> {
    private final Object lock;
    private final Map<String, ActionLink> body;
    private final Map<String, String> parents;
    private ActionLink head;
    private ActionLink tail;

    public LinkedPipeline() {
        lock = new Object();
        body = new HashMap<>();
        parents = new HashMap<>();
    }

    @Override
    public Object execute(Object o) throws Exception {
        Object data = o;
        ActionLink cur = head;
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
    public Action<?, ?> get(String key) {
        ActionLink ret = body.get(key);
        if (ret != null) {
            return ret.getBody();
        }
        return null;
    }

    @Override
    public Action<?, ?> put(String key, Action<?, ?> value) {
        synchronized (lock) {
            ActionLink toAdd = new ActionLink(key, value);
            ActionLink ret = body.put(key, toAdd);
            if (ret == null) {
                if (head == null) {
                    head = toAdd;
                } else {
                    tail.attach(toAdd);
                    parents.put(key, tail.getName());
                }
                tail = toAdd;
                return null;
            }
            if (ret == head) {
                head = toAdd;
                toAdd.attach(ret.detach());
            } else if (ret == tail) {
                tail = toAdd;
                body.get(parents.get(ret.getName())).attach(toAdd);
            }
            return ret.getBody();
        }
    }

    @Override
    public Action<?, ?> remove(String key) {
        synchronized (lock) {
            ActionLink ret = body.remove(key);
            if (ret == null) {
                return null;
            }
            ActionLink tail = ret.detach();
            if (ret == head) {
                head = tail;
                if (tail == null) {
                    this.tail = null;
                } else {
                    parents.remove(tail.getName());
                }
            } else {
                ActionLink parent = body.get(parents.remove(key));
                if (ret == this.tail) {
                    parent.detach();
                    this.tail = parent;
                } else {
                    parents.put(tail.getName(), parent.getName());
                    parent.attach(tail);
                }
            }
            return ret.getBody();
        }
    }

    private void insert(String key, ActionLink value, boolean after) {
        Objects.requireNonNull(key);
        if (value == null) {
            return;
        }
        ActionLink pos = body.get(key);
        Objects.requireNonNull(pos);
        if (after) {
            ActionLink child = pos.detach();
            value.attach(child);
            pos.attach(value);
            if (pos == tail) {
                tail = value;
            } else {
                parents.put(child.getName(), value.getName());
            }
            parents.put(value.getName(), key);
        } else {
            value.attach(pos);
            if (pos == head) {
                head = value;
            } else {
                ActionLink parent = body.get(parents.remove(key));
                parent.attach(value);
                parents.put(value.getName(), parent.getName());
            }
            parents.put(key, value.getName());
        }
    }

    private void insert(String key, String insertKey, Action<?, ?> value, boolean after) {
        if (body.containsKey(insertKey)) {
            throw new IllegalStateException("Pipeline already contains key " + insertKey + "!");
        }
        synchronized (lock) {
            ActionLink toInsert = new ActionLink(insertKey, value);
            body.put(insertKey, toInsert);
            insert(key, toInsert, after);
        }
    }

    @Override
    public void insertAfter(String key, String insertKey, Action<?, ?> value) {
        insert(key, insertKey, value, true);
    }

    @Override
    public void insertBefore(String key, String insertKey, Action<?, ?> value) {
        insert(key, insertKey, value, false);
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
    public Iterator<Node<String, Action<Object, Object>>> iterator() {
        return new LinkedPipelineIterator(head);
    }

    @Override
    public String toString() {
        return body.toString();
    }

    private static class LinkedPipelineIterator implements Iterator<Node<String, Action<Object, Object>>> {
        private ActionLink head;

        private LinkedPipelineIterator(ActionLink head) {
            this.head = head;
        }

        @Override
        public boolean hasNext() {
            return head != null;
        }

        @Override
        public Node<String, Action<Object, Object>> next() {
            if (head == null) {
                throw new NoSuchElementException();
            }
            ActionLink ret = head;
            head = head.tail();
            return new Node<>(ret.getName(), ret.getBody());
        }
    }
}
