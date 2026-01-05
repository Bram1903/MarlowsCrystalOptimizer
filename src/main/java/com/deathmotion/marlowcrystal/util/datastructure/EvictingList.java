package com.deathmotion.marlowcrystal.util.datastructure;

import lombok.Getter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Getter
public class EvictingList<T> extends LinkedList<T> {
    private final int maxSize;

    public EvictingList(int maxSize) {
        this.maxSize = maxSize;
    }

    public EvictingList(Collection<? extends T> c, int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= maxSize && maxSize > 0) removeFirst();
        return super.add(t);
    }

    public List<T> getLast(int count) {
        if (count <= 0) {
            return List.of();
        }

        int fromIndex = Math.max(0, size() - count);
        return new LinkedList<>(subList(fromIndex, size()));
    }

}
