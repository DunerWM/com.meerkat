package com.meerkat.base.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by wm on 17/4/18.
 */
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext ctx = null;

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public void setApplicationContext(ApplicationContext ctx)
            throws BeansException {
        ApplicationContextProvider.ctx = ctx;
    }
}
