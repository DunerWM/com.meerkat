package com.meerkat.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wm on 17/4/18.
 */
public class SerializeUtil {

    private static Logger log = LoggerFactory.getLogger(SerializeUtil.class);

    public static byte[] serialize(Object object) {
        if (object instanceof String) {
            return ((String) object).getBytes(StandardCharsets.UTF_8);
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            new ObjectOutputStream(outputStream).writeObject(object);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("序列化出错", e);
        }
    }

    public static Object unSerialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        } catch (StreamCorruptedException e) {
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("反序列化出错", e);
        }
    }

    @Deprecated
    /**
     * 拼写错误.请使用
     * cn.huoqiu.base.util.SerializeUtil#unSerialize(byte[])
     *
     */
    public static Object unserialize(byte[] bytes) {
        return unSerialize(bytes);
    }

    public static <K, V> Map<byte[], byte[]> parseFromObjectMap(Map<K, V> objectMap) {
        Map<byte[], byte[]> map = new HashMap<>();
        if (null == objectMap || 0 == objectMap.size()) {
            return map;
        }
        for (Map.Entry<K, V> entry : objectMap.entrySet()) {
            K k = entry.getKey();
            V v = entry.getValue();
            map.put(SerializeUtil.serialize(k), SerializeUtil.serialize(v));
        }
        return map;
    }

    public static List<byte[]> parseFromObjectList(List<? extends Object> objectList) {
        List<byte[]> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(objectList)) {
            return list;
        }
        for (Object obj : objectList) {
            list.add(SerializeUtil.serialize(obj));
        }
        return list;
    }

}
