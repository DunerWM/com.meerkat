package com.meerkat.base.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by wm on 16/9/27.
 */
public class ConfigPropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConfigPropertiesUtil.class);

    private static ResourceBundle bundle = null;


    static {
        bundle = ResourceBundle.getBundle("config", Locale.CHINESE);
    }

    public static long getLong(String key) {
        return getLong(key, -1L);
    }

    public static long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(getValue(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    private static boolean getBoolean(String key, boolean defaultValue) {
        String value = getValue(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Boolean.valueOf(value);
    }

    public static long getInt(String key) {
        return getLong(key, -1);
    }

    public static long getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getValue(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getValue(String key) {
        try {
            String value = bundle.getString(key);
            if (StringUtils.isBlank(value)) {
                return value;
            } else {
                return new String(value.getBytes("ISO-8859-1"), "UTF-8");
            }
        } catch (Exception e) {
            //忽略这个错误
            return null;
        }
    }
}
