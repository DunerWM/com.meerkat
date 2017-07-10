package com.meerkat.api.util;

import java.security.MessageDigest;

/**
 * md5加密工具
 * Created by Friedemann Lee on 2017-03-26 12:16.
 */
public class MD5Util {
    public static String get32(String source){
        if(source == null)return null;
        StringBuilder sb = new StringBuilder(32);
        try {
            MessageDigest md    = MessageDigest.getInstance("MD5");
            byte[] array        = md.digest(source.getBytes("utf-8"));
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).toUpperCase().substring(1, 3));
            }
        } catch (Exception e) {
            return null;
        }
        return sb.toString();
    }
}
