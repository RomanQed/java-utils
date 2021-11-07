package com.github.romanqed.jutils.structs;

import java.util.Iterator;
import java.util.Objects;

public class Link<E> implements Linkable<E> {
    private E value;
    private Linkable<E> tail;

    public Link(E value) {
        this.value = value;
    }

    public Link() {
    }

    @Override
    public void attach(Linkable<E> tail) {
        this.tail = Objects.requireNonNull(tail);
    }

    @Override
    public Linkable<E> detach() {
        Linkable<E> ret = tail;
        tail = null;
        return ret;
    }

    @Override
    public Linkable<E> tail() {
        return tail;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Linkable<E> head = Link.this;
            public Linkable<E> ptr = Link.this;

            @Override
            public boolean hasNext() {
                return ptr != null;
            }

            @Override
            public E next() {
                Linkable<E> ret = ptr;
                if (hasNext()) {
                    ptr = ptr.tail() == head ? null : ptr.tail();
                } else {
                    return null;
                }
                return ret.getValue();
            }
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Linkable) {
            Object value = ((Linkable<?>) obj).getValue();
            if (value == this.value) {
                return true;
            }
            return value != null && value.equals(this.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + value + "} -> " + "{" + (tail() == null ? null : tail().getValue()) + "}";
    }
}
