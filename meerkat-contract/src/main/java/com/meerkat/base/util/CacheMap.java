package com.meerkat.base.util;

/**
 * Created by wm on 17/4/18.
 */

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存Map,根据简单的LRU算法
 *
 * @version 1.0.0
 * @date 2015-12-22
 */
public class CacheMap<K, V> {
    /**
     * 默认缓存容量
     */
    private final static int DEFAULT_INIT_SIZE = 256;
    private final static int DEFAULT_REDO_COUNT = 10;

    /**
     * 缓存失效时长(毫秒)
     */
    private long expireMs;
    /**
     * 获取缓存时等待时长
     */
    private long waitingMs = 0;
    private long sleepMs = 0;
    private int redoCount = 10;

    private LRUMap<K, Entry<V>> valueMap;


    /**
     * 带失效时长的构造方法
     *
     * @param expireMs 失效时长
     */
    public CacheMap(long expireMs) {
        this(expireMs, DEFAULT_INIT_SIZE);
    }


    /**
     * 带失效时长和初始大小的构造方法
     *
     * @param expireMs 失效时长
     * @param initSize 初始大小
     */
    public CacheMap(long expireMs, int initSize) {
        this(expireMs, initSize, 0);
    }

    /**
     * 带失效时长和初始大小和等待时长的构造方法
     *
     * @param expireMs 失效时长
     * @param initSize 初始大小
     */
    public CacheMap(long expireMs, int initSize, long waitingMs) {
        this.expireMs = expireMs;
        this.waitingMs = waitingMs;
        this.sleepMs = waitingMs / DEFAULT_REDO_COUNT;
        this.redoCount = waitingMs > 0 ? DEFAULT_REDO_COUNT : 0;
        valueMap = new LRUMap(initSize);
    }


    /**
     * 从缓存中得到数据
     *
     * @param key
     * @return
     */
    public V get(K key) {
        Object value;
        int i = 0;
        do {
            Long accessTime = System.currentTimeMillis();
            Entry<V> entry = valueMap.get(key);
            if (entry == null) {
                return null;
            } else if (accessTime - entry.getTime() > expireMs) {
                valueMap.remove(key);
                return null;
            }
            value = entry.getObj();
            if (value instanceof WaitingObject) {
                if (waitingMs > 0) {
                    //类型不匹配,则等待一会
                    try {
                        TimeUnit.MILLISECONDS.sleep(sleepMs);
                    } catch (InterruptedException e1) {
                        //ignore
                    }
                } else {
                    break;
                }
            } else {
                entry.updateTime();
                return (V) value;
            }
        } while (i++ < redoCount);
        return null;
    }


    /**
     * 添加数据到缓存前,置为正在添加
     *
     * @param key
     */
    public void prePut(K key) {
        valueMap.put(key, new Entry(WaitingObject.instance));
    }

    /**
     * 清除等待对象
     *
     * @param key
     */
    public void cleanWaitingObject(K key) {
        del(key);
    }


    /**
     * 添加数据到缓存中
     *
     * @param key
     * @param value
     */
    public void put(K key, V value) {
        valueMap.put(key, new Entry(value));
    }

    public void del(K key) {
        valueMap.remove(key);
    }


    /**
     * 未做同步的size方法。在多线程情况下获取的的值可能不准确。
     */
    public int size() {
        return keySet().size();
    }

    public Set<K> keySet() {
        //遍历，将已过期的key删除后再返回存活的key
        Object[] keySet = valueMap.keySet().toArray();
        for (Object key : keySet) {
            get((K) key);
        }
        return valueMap.keySet();
    }

    /**
     * 缓存中存放的实体。
     *
     * @author <a href=mailto:liaoluping@huoqiu.cn>liaoluping</a>
     * @version 1.0.0
     * @date 2015-12-22
     */
    static class Entry<T> {
        /**
         * 最近访问时间
         */
        private long time;
        private T obj;


        /**
         * @param obj
         */
        private Entry(T obj) {
            this.time = System.currentTimeMillis();
            this.obj = obj;
        }


        /**
         * 更新最近访问时间为当前时间。
         */
        public void updateTime() {
            time = System.currentTimeMillis();
        }


        public long getTime() {
            return time;
        }


        public T getObj() {
            return obj;
        }

    }

    static class WaitingObject {
        private static WaitingObject instance = new WaitingObject();

        public WaitingObject getInstance() {
            return instance;
        }
    }

}
