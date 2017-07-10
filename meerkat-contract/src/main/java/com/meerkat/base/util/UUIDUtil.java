package com.meerkat.base.util;

import java.util.UUID;

/**
 * Created by wm on 17/4/18.
 */
public class UUIDUtil {

    /**
     * 获取唯一UID字符串
     *
     * @return
     */
    public static synchronized String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }

}
