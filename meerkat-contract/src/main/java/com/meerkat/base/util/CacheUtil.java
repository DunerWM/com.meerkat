package com.meerkat.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wm on 17/4/18.
 */
public class CacheUtil {

    private static final Logger logger = LoggerFactory.getLogger(CacheUtil.class);

    public static final String REDIS_IP = "redis.ip";
    public static final String REDIS_PORT = "redis.port";
    public static final String REDIS_TIMEOUT = "redis.timeout";
    private static String ip;
    private static int port;
    private static int timeOut;

    public static final int HALF_YEAR = 60 * 60 * 24 * 180;


    static {
        ip = ConfigPropertiesUtil.getValue(REDIS_IP);
        port = Integer.parseInt(ConfigPropertiesUtil.getValue(REDIS_PORT));
        try {
            timeOut = Integer.parseInt(ConfigPropertiesUtil.getValue(REDIS_TIMEOUT));
        } catch (Throwable th) {
            logger.info("no config for {}, use default value: {}", REDIS_TIMEOUT, Jedis.DEFAULT_TIMEOUT);
            timeOut = Jedis.DEFAULT_TIMEOUT;
        }
    }

    public static redis.clients.jedis.Jedis getJedis() {
        redis.clients.jedis.Jedis jedis = Jedis.getInstance().getJedis(ip, port, timeOut);
        if (jedis == null) {
            throw new JedisException("can't get jedis with ip/port: " + ip + "/" + port);
        }
        return jedis;
    }

    private static void closeJedis(redis.clients.jedis.Jedis jedis) {
        if (jedis == null) {
            return;
        }
        Jedis.getInstance().closeJedis(jedis, ip, port, timeOut);
    }


    /**
     * 将该对象的过期时间设置为 seconds
     *
     * @param key
     * @param seconds
     * @return
     */
    public static Long expire(String key, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.expire(key.getBytes(), seconds);
        } finally {
            closeJedis(jedis);
        }
    }


    public static boolean setString(String key, String value) {
        return setString(key, value, HALF_YEAR);
    }

    public static boolean setString(String key, String value, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.getSet(key, value);
            jedis.expire(key, seconds);
            return true;
        } finally {
            closeJedis(jedis);
        }
    }

    public static boolean setStringIfNotExists(String key, String value) {
        return setStringIfNotExists(key, value, HALF_YEAR);
    }

    public static boolean setStringIfNotExists(String key, String value, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.setnx(key, value);
            jedis.expire(key, seconds);
            return true;
        } finally {
            closeJedis(jedis);
        }
    }

    public static boolean set(String key, Object value, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key.getBytes(), SerializeUtil.serialize(value));
            jedis.expire(key.getBytes(), seconds);
            return true;
        } finally {
            closeJedis(jedis);
        }
    }

    public static boolean set(String key, Object value) {
        return set(key, value, HALF_YEAR);
    }

    public static String getString(String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        } finally {
            closeJedis(jedis);
        }
    }

    public static <T> T get(String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            byte[] obj = jedis.get(key.getBytes());
            return (T) (obj == null ? null : SerializeUtil.unSerialize(obj));
        } finally {
            closeJedis(jedis);
        }
    }

    public static void hsetString(String key, String field, String value, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hset(key, field, value);
            jedis.expire(key, seconds);
        } finally {
            closeJedis(jedis);
        }
    }

    public static void hsetBean(String key, Object field, Object object, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hset(key.getBytes(), SerializeUtil.serialize(field), SerializeUtil.serialize(object));
            jedis.expire(key.getBytes(), seconds);
        } finally {
            closeJedis(jedis);
        }
    }

    public static void hmsetStrings(String key, Map<String, String> map, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hmset(key, map);
            jedis.expire(key, seconds);
        } finally {
            closeJedis(jedis);
        }
    }

    public static void hmsetBeans(String key, Map<? extends Object, ? extends Object> beanMap, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();

            Map<byte[], byte[]> map = SerializeUtil.parseFromObjectMap(beanMap);
            jedis.hmset(key.getBytes(), map);
            jedis.expire(key, seconds);
        } finally {
            closeJedis(jedis);
        }
    }

    public static String hmgetString(String key, String field) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hget(key, field);
        } finally {
            closeJedis(jedis);
        }
    }


    public static <T> T hmgetBean(String key, Object field) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            byte[] b = jedis.hget(key.getBytes(), SerializeUtil.serialize(field));
            if (null != b) {
                return (T) SerializeUtil.unSerialize(b);
            } else return null;
        } finally {
            closeJedis(jedis);
        }
    }

    public static <T> T hmgetBean(String key, byte[] field) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            byte[] b = jedis.hget(key.getBytes(), field);
            if (null != b) {
                return (T) SerializeUtil.unSerialize(b);
            } else return null;
        } finally {
            closeJedis(jedis);
        }
    }


    public static List<String> hmgetStrings(String key, List<String> fields) {
        List<String> tList;
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            String[] array = fields.toArray(new String[]{});
            tList = jedis.hmget(key, array);
            if (null == tList) {
                tList = new ArrayList<>();
            }
            return tList;
        } finally {
            closeJedis(jedis);
        }
    }


    public static <T> List<T> hmgetBeans(String key, List<? extends Object> fields) {
        redis.clients.jedis.Jedis jedis = null;
        List<T> tList = new ArrayList<>();
        try {
            jedis = getJedis();
            List<byte[]> bytes = null;
            if (null == fields || 0 == fields.size())
                return tList;

            if (fields.get(0) instanceof byte[]) {
                bytes = (List<byte[]>) fields;
            } else {
                bytes = SerializeUtil.parseFromObjectList(fields);
            }
            List<byte[]> list = jedis.hmget(key.getBytes(), bytes.toArray(new byte[][]{}));
            if (null != list && list.size() > 0) {
                for (byte[] b : list) {
                    T t = (T) SerializeUtil.unSerialize(b);
                    if (null != t) {
                        tList.add(t);
                    }
                }
            }
            if (null == list) {
                tList = new ArrayList<>();
            }
            return tList;
        } finally {
            closeJedis(jedis);
        }
    }


    public static List<? extends Object> lrange(String key, long start, long end) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            List<byte[]> l = jedis.lrange(key.getBytes(), start, end);
            if (l == null || l.size() == 0) {
                logger.info("lrange {},{},{} list is  null or empty. return null", key, start, end);
                return null;
            }
            List<? super Object> objList = new ArrayList();
            for (byte[] by : l) {
                objList.add(SerializeUtil.unSerialize(by));
            }
            return objList;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 使指定key的值增加1.不改变key的过期时间.
     * 如果指定key不存在,则按照<code>0</code>处理.
     * 如果指定的key已存在,则值必须为数字内容的字符串,即调用过setString方法进行赋值,否则抛出异常.
     *
     * @param key
     * @return 运算后的结果
     */
    public static Long incr(String key) {
        return incrBy(key, 1);
    }

    /**
     * 使指定key的值增加1.改变key的过期时间.
     * 如果指定key不存在,则按照<code>0</code>处理.
     * 如果指定的key已存在,则值必须为数字内容的字符串,即调用过setString方法进行赋值,否则抛出异常.
     *
     * @param key
     * @return 运算后的结果
     */
    public static Long incr(String key, int seconds) {
        return incrBy(key, 1, seconds);
    }

    /**
     * 使指定key的值增加<code>number</code>.不改变key的过期时间.
     * 如果指定key不存在,则按照<code>0</code>处理.
     * 如果指定的key已存在,则值必须为数字内容的字符串,即调用过setString方法进行赋值,否则抛出异常.
     *
     * @param key
     * @return 运算后的结果
     */
    public static Long incrBy(String key, long number) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            Long value = jedis.incrBy(key, number);
            return value;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 使指定key的值增加<code>number</code>.改变key的过期时间.
     * 如果指定key不存在,则按照<code>0</code>处理.
     * 如果指定的key已存在,则值必须为数字内容的字符串,即调用过setString方法进行赋值,否则抛出异常.
     *
     * @param key
     * @return 运算后的结果
     */
    public static Long incrBy(String key, long number, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            Long value = jedis.incrBy(key, number);
            jedis.expire(key, seconds);
            return value;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 使指定key的值减少<code>number</code>.不改变key的过期时间.
     * 如果指定key不存在,则按照<code>0</code>处理.
     * 如果指定的key已存在,则值必须为数字内容的字符串,即调用过setString方法进行赋值,否则抛出异常.
     *
     * @param key
     * @return 运算后的结果
     */
    public static Long decrBy(String key, long number) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            Long value = jedis.decrBy(key, number);
            return value;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 使指定key的值减少<code>number</code>.改变key的过期时间.
     * 如果指定key不存在,则按照<code>0</code>处理.
     * 如果指定的key已存在,则值必须为数字内容的字符串,即调用过setString方法进行赋值,否则抛出异常.
     *
     * @param key
     * @return 运算后的结果
     */
    public static Long decrBy(String key, long number, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            Long value = jedis.decrBy(key, number);
            jedis.expire(key, seconds);
            return value;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 使指定key的值减少1.不改变key的过期时间.
     * 如果指定key不存在,则按照<code>0</code>处理.
     * 如果指定的key已存在,则值必须为数字内容的字符串,即调用过setString方法进行赋值,否则抛出异常.
     *
     * @param key
     * @return 运算后的结果
     */
    public static Long decr(String key) {
        return decrBy(key, 1);
    }

    /**
     * 使指定key的值减少1.改变key的过期时间.
     * 如果指定key不存在,则按照<code>0</code>处理.
     * 如果指定的key已存在,则值必须为数字内容的字符串,即调用过setString方法进行赋值,否则抛出异常.
     *
     * @param key
     * @return 运算后的结果
     */
    public static Long decr(String key, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            Long value = jedis.decr(key);
            jedis.expire(key, seconds);
            return value;
        } finally {
            closeJedis(jedis);
        }
    }


    public static long del(String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(key.getBytes());
        } finally {
            closeJedis(jedis);
        }
    }

    public static long hdel(String hashKey, String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hdel(hashKey, key);
        } finally {
            closeJedis(jedis);
        }
    }

    public static Long rpush(String key, Object value) {
        return rpush(key, value, HALF_YEAR);
    }

    public static Long rpush(String key, Object value, int second) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            Long returnValue = jedis.rpush(key.getBytes(), SerializeUtil.serialize(value));
            jedis.expire(key.getBytes(), second);
            return returnValue;
        } finally {
            closeJedis(jedis);
        }
    }

    public static Long lpush(String key, Object value) {
        return lpush(key, value, HALF_YEAR);
    }

    public static Long lpush(String key, Object value, int second) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            Long returnValue = jedis.lpush(key.getBytes(), SerializeUtil.serialize(value));
            jedis.expire(key.getBytes(), second);
            return returnValue;
        } finally {
            closeJedis(jedis);
        }
    }

    public static <T> T lpop(String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            byte[] obj = jedis.lpop(key.getBytes());
            return (T) (obj == null ? null : SerializeUtil.unSerialize(obj));
        } finally {
            closeJedis(jedis);
        }
    }

    public static <T> T rpop(String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            byte[] obj = jedis.rpop(key.getBytes());
            return (T) (obj == null ? null : SerializeUtil.unSerialize(obj));
        } finally {
            closeJedis(jedis);
        }
    }

    public static <T> T blpop(int timeout, String key) {   //优化实现
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            List<byte[]> result = jedis.blpop(timeout, key.getBytes());
            if (result == null || result.isEmpty())
                return null;
            else {
                return (T) SerializeUtil.unSerialize(result.get(1));
            }
        } finally {
            closeJedis(jedis);
        }
    }

    public static <T> T brpop(int timeout, String key) {   //优化实现
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();

            List<byte[]> result = jedis.brpop(timeout, key.getBytes());

            if (result.isEmpty()) {
                return null;
            } else {
                return (T) SerializeUtil.unSerialize(result.get(1));
            }
        } finally {
            closeJedis(jedis);
        }
    }

    public static long llen(String key) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.llen(key.getBytes());
        } finally {
            closeJedis(jedis);
        }
    }

    public static <T> T hmget(byte[] key, byte[] field, Class<T> tClass) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            List<byte[]> lst = jedis.hmget(key, field);
            if (lst.get(0) != null) {
                return (T) SerializeUtil.unSerialize((byte[]) lst.get(0));
            } else return null;
        } finally {
            closeJedis(jedis);
        }
    }

    public static <T> T hget(String key, String field, Class<T> tClass) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            String value = jedis.hget(key, field);
            if (!StringUtils.isEmpty(value)) {
                return JsonUtil.load(value, tClass);
            }
            return null;
        } finally {
            closeJedis(jedis);
        }
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void setTimeOut(int timeOut) {
        CacheUtil.timeOut = timeOut;
    }

    public static Long hIncr(String key, String field, int seconds) {
        return hIncrBy(key, field, 1, seconds);
    }

    public static Long hIncrBy(String key, String field, long value, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            Long result = jedis.hincrBy(key, field, value);
            jedis.expire(key, seconds);
            return result;
        } finally {
            closeJedis(jedis);
        }
    }


    public static Double hIncrBy(String key, String field, double value, int seconds) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            jedis = getJedis();
            Double result = jedis.hincrByFloat(key, field, value);
            jedis.expire(key, seconds);
            return result;
        } finally {
            closeJedis(jedis);
        }
    }

    public static Set<String> keys(String pattern) {
        redis.clients.jedis.Jedis jedis = null;
        try {
            return getJedis().keys(pattern);
        } finally {
            closeJedis(jedis);
        }
    }

}
