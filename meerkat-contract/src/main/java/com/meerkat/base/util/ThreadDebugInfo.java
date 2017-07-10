package com.meerkat.base.util;

/**
 * Created by wm on 17/4/18.
 */
public class ThreadDebugInfo {

    private static ThreadLocal<DebugInfo> threadLocal = new ThreadLocal<>();

    public static DebugInfo get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }

    public static void set(DebugInfo debugInfo) {
        threadLocal.set(debugInfo);
    }

}
