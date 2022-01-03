package com.github.romanqed.jutils.structs;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Pipeline implements Map<String, Function<?, ?>> {
    private final Object lock;
    private final Map<String, ActionLink> body;
    private final Map<String, String> parents;
    private ActionLink tail;
    private ActionLink head;

    public Pipeline() {
        lock = new Object();
        body = new ConcurrentHashMap<>();
        parents = new ConcurrentHashMap<>();
    }

    public PipelineResult process(Object data) {
        ActionLink cur = head;
        while (cur != null) {
            try {
                data = cur.getBody().apply(data);
            } catch (PipelineInterruptException e) {
                return new PipelineResult(e.getBody(), true);
            }
            cur = cur.tail();
        }
        return new PipelineResult(data, false);
    }

    private void insert(String key, ActionLink start, ActionLink end, boolean after) {
        Objects.requireNonNull(key);
        if (start == null) {
            return;
        }
        ActionLink pos = body.get(key);
        Objects.requireNonNull(pos);
        if (after) {
            ActionLink child = pos.detach();
            end.attach(child);
            pos.attach(start);
            if (pos == tail) {
                tail = end;
            } else {
                parents.put(child.getName(), end.getName());
            }
            parents.put(start.getName(), key);
        } else {
            end.attach(pos);
            if (pos == head) {
                head = start;
            } else {
                ActionLink parent = body.get(parents.remove(key));
                parent.attach(start);
                parents.put(start.getName(), parent.getName());
            }
            parents.put(key, end.getName());
        }
    }

    private void check(Pipeline pipeline) {
        for (String key : pipeline.body.keySet()) {
            if (body.containsKey(key)) {
                throw new IllegalStateException("Pipeline already contains key " + key + "!");
            }
        }
    }

    public void insert(String position, Pipeline value, boolean after) {
        Objects.requireNonNull(value);
        check(value);
        synchronized (lock) {
            ActionLink start;
            ActionLink end;
            synchronized (value.lock) {
                ActionLink cur = value.head;
                start = value.head.duplicate();
                end = start;
                while (cur.tail() != null) {
                    body.put(cur.getName(), cur);
                    parents.put(cur.tail().getName(), cur.getName());
                    end.attach(cur.tail().duplicate());
                    end = end.tail();
                    cur = cur.tail();
                }
                body.put(cur.getName(), cur);
            }
            insert(position, start, end, after);
        }
    }

    public void insert(String position, String key, Function<?, ?> value, boolean after) {
        if (body.containsKey(key)) {
            throw new IllegalStateException("Pipeline already contains key " + key + "!");
        }
        synchronized (lock) {
            ActionLink toInsert = new ActionLink(key, value);
            body.put(key, toInsert);
            insert(position, toInsert, toInsert, after);
        }
    }

    public void insertAfter(String after, String key, Function<?, ?> value) {
        insert(after, key, value, true);
    }

    public void insertBefore(String before, String key, Function<?, ?> value) {
        insert(before, key, value, false);
    }

    public void insertAfter(String after, Pipeline value) {
        insert(after, value, true);
    }

    public void insertBefore(String before, Pipeline value) {
        insert(before, value, false);
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
    public boolean containsKey(Object key) {
        return body.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return body.containsValue(value);
    }

    @Override
    public Function<Object, Object> get(Object key) {
        ActionLink ret = body.get(key);
        if (ret != null) {
            return ret.getBody();
        }
        return null;
    }

    @Override
    public Function<?, ?> put(String key, Function<?, ?> value) {
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
    public Function<Object, Object> remove(Object key) {
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

    @Override
    public void putAll(Map<? extends String, ? extends Function<?, ?>> m) {
        Objects.requireNonNull(m);
        m.forEach(this::put);
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
    public Set<String> keySet() {
        return body.keySet();
    }

    @Override
    public Collection<Function<?, ?>> values() {
        return body.values().stream().map(ActionLink::getBody).collect(Collectors.toList());
    }

    @Override
    public Set<Entry<String, Function<?, ?>>> entrySet() {
        return body.entrySet().stream().map(e -> new Entry<String, Function<?, ?>>() {

            @Override
            public String getKey() {
                return e.getKey();
            }

            @Override
            public Function<?, ?> getValue() {
                return e.getValue().getBody();
            }

            @Override
            public Function<?, ?> setValue(Function<?, ?> value) {
                throw new UnsupportedOperationException("Unsupported operation: setValue!");
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return body.toString();
    }

    static class PipelineResult {
        private final boolean interrupted;
        private final Object result;

        protected PipelineResult(Object result, boolean interrupted) {
            this.result = result;
            this.interrupted = interrupted;
        }

        public Object getResult() {
            return result;
        }

        public boolean isInterrupted() {
            return interrupted;
        }
    }
}
