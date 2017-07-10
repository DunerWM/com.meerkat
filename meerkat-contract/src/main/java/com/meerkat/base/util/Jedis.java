package com.meerkat.base.util;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wm on 17/4/18.
 */
public class Jedis {

    public static final int DEFAULT_TIMEOUT = 2000;
    private static Map<String, JedisPool> maps = new HashMap<String, JedisPool>();

    private Jedis() {

    }


    private static JedisPool getPool(String ip, int port) {
        return getPool(ip, port, DEFAULT_TIMEOUT);
    }

    /**
     * 获取连接池.
     *
     * @return 连接池实例
     */
    private static JedisPool getPool(String ip, int port, int timeOut) {
        String key = ip + ":" + port + ":" + timeOut;
        JedisPool pool = null;
        if (!maps.containsKey(key)) {
            JedisPoolConfig config = new JedisPoolConfig();
            //config.setMaxActive();
            //config.setMaxIdle();
            //config.setMaxWait();
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            try {
                /**
                 *如果遇到 java.net.SocketTimeoutException: Read timed out exception的异常信息
                 *请尝试在构造JedisPool的时候设置自己的超时值. JedisPool默认的超时时间是2秒(单位毫秒)
                 */
                pool = new JedisPool(config, ip, port, timeOut);
                maps.put(key, pool);
            } catch (Exception e) {

                e.printStackTrace();
            }
        } else {
            pool = maps.get(key);
        }
        return pool;
    }

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。
     */
    private static class RedisUtilHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static Jedis instance = new Jedis();
    }

    public redis.clients.jedis.Jedis getJedis(String ip, int port) {
        return getJedis(ip, port, DEFAULT_TIMEOUT);
    }

    /**
     * 当getInstance方法第一次被调用的时候，它第一次读取
     * RedisUtilHolder.instance，导致RedisUtilHolder类得到初始化；而这个类在装载并被初始化的时候，会初始化它的静
     * 态域，从而创建RedisUtil的实例，由于是静态的域，因此只会在虚拟机装载类的时候初始化一次，并由虚拟机来保证它的线程安全性。
     * 这个模式的优势在于，getInstance方法并没有被同步，并且只是执行一个域的访问，因此延迟初始化并没有增加任何访问成本。
     */
    public static Jedis getInstance() {
        return RedisUtilHolder.instance;
    }

    /**
     * 获取Redis实例.
     *
     * @return Redis工具类实例
     */
    public redis.clients.jedis.Jedis getJedis(String ip, int port, int timeOut) {
        redis.clients.jedis.Jedis jedis = null;
        int count = 0;

        do {
            try {
                jedis = getPool(ip, port, timeOut).getResource();
            } catch (Exception e) {
                getPool(ip, port, timeOut).returnBrokenResource(jedis);
            }
            count++;
        } while (jedis == null && count < 5);

        return jedis;
    }


    public void closeJedis(redis.clients.jedis.Jedis jedis, String ip, int port) {
        closeJedis(jedis, ip, port, DEFAULT_TIMEOUT);
    }

    /**
     * 释放jedis实例到连接池
     *
     * @param jedis redis实例
     */
    public void closeJedis(redis.clients.jedis.Jedis jedis, String ip, int port, int timeOut) {
        if (jedis != null) {
            try {
                getPool(ip, port, timeOut).returnResource(jedis);
            } catch (Exception e) {
                //TODO: 会异常，不过不影响使用
            }
        }
    }

}
