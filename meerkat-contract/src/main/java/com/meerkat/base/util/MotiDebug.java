package com.meerkat.base.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wm on 17/4/27.
 */
public class MotiDebug {

    private static final String DEBUG_PARAME_NAME = "huoqiu_debug";
    private static final String TRACE_PARAME_NAME = "huoqiu_trace";
    private static final String DEBUG_TURN_ON_VALUE = "_true_";
    private static final String TRACE_TURN_ON_VALUE = "_true_";

    public static boolean isDebug(HttpServletRequest request) {
        return isTrace(request) || StringUtils.equals(request.getParameter(DEBUG_PARAME_NAME), DEBUG_TURN_ON_VALUE);
    }

    public static boolean isFromDebug(HttpServletRequest request) {
        return isFromTrace(request) || isFrom(request, DEBUG_PARAME_NAME, DEBUG_TURN_ON_VALUE);
    }


    public static boolean isTrace(HttpServletRequest request) {
        return StringUtils.equals(request.getParameter(TRACE_PARAME_NAME), TRACE_TURN_ON_VALUE);
    }

    public static boolean isFromTrace(HttpServletRequest request) {
        return isFrom(request, TRACE_PARAME_NAME, TRACE_TURN_ON_VALUE);
    }


    private static boolean isFrom(HttpServletRequest request, String from, String value) {
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains(from)) {
            String paramString = referer.substring(referer.indexOf("?") + 1);
            if (StringUtils.isNotBlank(paramString)) {
                String[] paramArray = paramString.split("&");
                if (paramArray != null && paramArray.length > 0) {
                    for (String param : paramArray) {
                        String[] nameAndValue = param.split("=");
                        if (nameAndValue != null && nameAndValue.length == 2 && StringUtils.equalsIgnoreCase(from, nameAndValue[0]) &&
                                StringUtils.equalsIgnoreCase(value, nameAndValue[1])) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
