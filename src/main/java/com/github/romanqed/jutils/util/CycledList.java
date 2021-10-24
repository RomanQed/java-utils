package com.github.romanqed.jutils.util;

import com.github.romanqed.ranges.CycledRange;

import java.util.*;


public class CycledList<T> extends ArrayList<T> {
    public CycledList(int initialCapacity) {
        super(initialCapacity);
    }

    public CycledList(Collection<? extends T> c) {
        super(c);
    }

    public CycledList() {
        super();
    }

    @SafeVarargs
    public static <E> CycledList<E> of(E... elements) {
        if (elements == null) {
            return null;
        }
        return new CycledList<>(Arrays.asList(elements));
    }

    @Override
    public T get(int index) {
        CycledRange range = new CycledRange(size());
        return super.get(range.indexOf(index));
    }

    @Override
    public T set(int index, T element) {
        CycledRange range = new CycledRange(size());
        return super.set(range.indexOf(index), element);
    }

    @Override
    public void add(int index, T element) {
        CycledRange range = new CycledRange(size() + 1);
        super.add(range.indexOf(index), element);
    }

    @Override
    public T remove(int index) {
        CycledRange range = new CycledRange(size());
        return super.remove(range.indexOf(index));
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        CycledRange range = new CycledRange(size() + 1);
        return super.addAll(range.indexOf(index), c);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        CycledRange range = new CycledRange(size() + 1);
        super.removeRange(range.indexOf(fromIndex), range.indexOf(toIndex));
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        CycledRange range = new CycledRange(size());
        return super.listIterator(range.indexOf(index));
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        CycledRange range = new CycledRange(size() + 1);
        return super.subList(range.indexOf(fromIndex), range.indexOf(toIndex));
    }
}
