package com.github.romanqed.jutils.structs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class Chain<E> implements Collection<E> {
    private int size;
    private Linkable<E> head;

    public Chain(E value) {
        if (value != null) {
            head = new Link<>(value);
            head.attach(head);
            size = 1;
        }
    }

    public Chain() {
        this(null);
    }

    private E removeNextFor(Linkable<E> link) {
        Linkable<E> toRemove;
        if (link.tail() == link) {
            toRemove = link;
            head = null;
            size = 0;
        } else {
            toRemove = link.tail();
            link.attach(toRemove.tail());
            size -= 1;
        }
        return toRemove.getValue();
    }

    public E next() {
        if (head == null) {
            return null;
        }
        Linkable<E> ret = head;
        head = head.tail();
        return ret.getValue();
    }

    public E remove() {
        if (head == null) {
            return null;
        }
        return removeNextFor(head);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size != 0;
    }

    @Override
    public boolean contains(Object o) {
        if (head == null) {
            return false;
        }
        for (E value : head) {
            if (value == o) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        if (head != null) {
            return head.iterator();
        }
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public E next() {
                return null;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] ret = new Object[size];
        Iterator<E> it = iterator();
        for (int i = 0; i < size; ++i) {
            ret[i] = it.next();
        }
        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        T[] ret = a.length < size ? Arrays.copyOf(a, size) : a;
        Iterator<E> it = iterator();
        for (int i = 0; i < size; ++i) {
            ret[i] = (T) it.next();
        }
        if (ret.length > size) {
            ret[size] = null;
        }
        return ret;
    }

    @Override
    public boolean add(E e) {
        if (head == null) {
            head = new Link<>(e);
            head.attach(head);
            size = 1;
            return true;
        }
        Link<E> toAdd = new Link<>(e);
        toAdd.attach(head.tail());
        head.attach(toAdd);
        size += 1;
        head = toAdd;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Linkable<E> ptr = head;
        for (int i = 0; i < size; ++i) {
            if (Objects.equals(ptr.tail().getValue(), o)) {
                removeNextFor(ptr);
                head = head.tail();
                return true;
            }
            ptr = ptr.tail();
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E o : c) {
            if (!add(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) {
            if (!remove(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for (E e : this) {
            if (!c.contains(e)) {
                if (!remove(e)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void clear() {
        size = 0;
        head = null;
    }
}
