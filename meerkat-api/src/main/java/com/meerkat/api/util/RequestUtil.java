package com.meerkat.api.util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wm on 17/5/7.
 */
public class RequestUtil {

    public static final Pattern QUERY_STRING_PASSWORD_PATTERN = Pattern.compile("((?:password|pwd)[^=&]*)=([^=&]*)", Pattern.CASE_INSENSITIVE);
    public static final Pattern PARAM_PASSWORD_PATTERN = Pattern.compile("(password|pwd|psw)", Pattern.CASE_INSENSITIVE);


    public static String getQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            return null;
        }

        Matcher m = QUERY_STRING_PASSWORD_PATTERN.matcher(queryString);
        return m.replaceAll("$1=******");
    }

    public static Map<String, String[]> getParameterArray(HttpServletRequest request) {
        return getParameterArray(request.getParameterMap());
    }


    public static Map<String, String[]> getParameterArray(Map<String, String[]> params) {
        Map<String, String[]> result = new HashMap<>(params);
        for (Map.Entry<String, String[]> entry : result.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            if (value == null || value.length == 0) {
                continue;
            }

            Matcher m = PARAM_PASSWORD_PATTERN.matcher(key);
            if (m.find()) {
                for (int i = 0; i < value.length; i++) {
                    value[i] = "******";
                }
                entry.setValue(value);
            }
        }
        return params;
    }


    public static Map<String, String> getParameterString(HttpServletRequest request) {
        return getParameterString(getParameterArray(request));

    }

    private static Map<String, String> getParameterString(Map<String, String[]> params) {
        Map<String, String> params0 = new HashMap<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String[] value = entry.getValue();
            if (value == null || value.length == 0) {
                continue;
            }
            params0.put(entry.getKey(), value[0]);
        }
        return params0;
    }

}
