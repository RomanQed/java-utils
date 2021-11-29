package com.github.romanqed.jutils.structs;

import java.util.Iterator;

public class ClosedChain<E extends Link> implements Chain<E> {
    private Link head;

    @Override
    public void add(E link) {
        if (head == null) {
            head = link;
            head.attach(head);
        } else {
            link.attach(head.tail());
            head.attach(link);
            head = link;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove() {
        if (head == null) {
            return null;
        }
        Link toRemove;
        if (head.tail() == head) {
            toRemove = head;
            head = null;
        } else {
            toRemove = head.tail();
            head.attach(toRemove.tail());
        }
        return (E) toRemove;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E next() {
        if (head == null) {
            return null;
        }
        Link ret = head;
        head = head.tail();
        return (E) ret;
    }

    @Override
    public void clear() {
        head = null;
    }

    @Override
    public Iterator<E> iterator() {
        return new IteratorImpl(head);
    }

    private class IteratorImpl implements Iterator<E> {
        private final Link head;
        private Link ptr;

        private IteratorImpl(Link head) {
            this.head = head;
            this.ptr = head;
        }

        @Override
        public boolean hasNext() {
            return ptr != null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            Link ret = ptr;
            if (hasNext()) {
                ptr = ptr.tail() == head ? null : ptr.tail();
            } else {
                return null;
            }
            return (E) ret;
        }
    }
}
