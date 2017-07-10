package com.meerkat.base.util;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 一个限定容量的Map
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1L;
    private final int maxCapacity;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    public LRUMap(int maxCapacity) {
        super(maxCapacity, 0.8F, false);
        this.maxCapacity = maxCapacity;
    }


    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }


    @Override
    public V get(Object key) {
        try {
            //这里，如果accessOrder=true,则使用写锁,是因为get后会重新排序,也就是会引起写操作
            lock.readLock().lock();
            return super.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }


    @Override
    public V put(K key, V value) {
        try {
            lock.writeLock().lock();
            return super.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public V remove(Object key) {
        try {
            lock.writeLock().lock();
            return super.remove(key);
        } finally {
            lock.writeLock().unlock();
        }

    }
}
