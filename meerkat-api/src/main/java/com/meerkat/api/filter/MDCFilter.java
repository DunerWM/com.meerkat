package com.meerkat.api.filter;

import com.meerkat.api.util.RequestUtil;
import com.meerkat.base.lang.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wm on 17/5/7.
 */
public class MDCFilter extends BaseFilter {
    static Logger log = LoggerFactory.getLogger(MDCFilter.class);

    public static final String LOCAL_IP_KEY = "localIp";
    public static final String REMOTE_IP_KEY = "remoteIp";
    public static final String USER_ID_KEY = "userId";
    public static final String REQUEST_ID_KEY = "requestId";
    public static final String THREAD_KEY = "thread";
    public static final String URI_KEY = "uri";
    public static final String UA_KEY = "ua";
    public static final String QUERY_STRING_KEY = "queryString";
    public static final String PARAMS_KEY = "params";
    public static final String APP_VERSION_KEY = "app-version";
    public static final String HOST_KEY = "host";

    private AtomicLong requestId = new AtomicLong(1);
    private Set<Pattern> excludeUrlPatterns = new HashSet<>();
    private Set<String> excludeUrls = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String urls = filterConfig.getInitParameter("logParamExcludeUrl");
        if (!Strings.isBlank(urls)) {
            for (String url : urls.split(",")) {
                url = url.trim();
                if (StringUtils.isNotBlank(url)) {
                    if (url.startsWith("^") || url.endsWith("$")) {
                        excludeUrlPatterns.add(Pattern.compile(url));
                    } else {
                        excludeUrls.add(url);
                    }

                }
            }
        }
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        addMDCContext(request);
        chain.doFilter(request, response);
    }

    //http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout
    private void addMDCContext(HttpServletRequest request) {
        MDC.put(LOCAL_IP_KEY, request.getLocalAddr());
        MDC.put(REQUEST_ID_KEY, String.valueOf(requestId.getAndIncrement()));
        MDC.put(REMOTE_IP_KEY, request.getRemoteAddr());
        MDC.put(THREAD_KEY, Thread.currentThread().getName());
        MDC.put(URI_KEY, request.getRequestURI() + "(" + request.getMethod() + ")");
        MDC.put(UA_KEY, request.getHeader("User-Agent"));
        MDC.put(APP_VERSION_KEY, request.getHeader("version"));
        MDC.put(HOST_KEY, request.getHeader("Host"));

        Object userId = request.getAttribute("userId");
        if (userId == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                userId = session.getAttribute("userId");
            }
        }
        if (userId != null) {
            MDC.put(USER_ID_KEY, userId.toString());
        } else {
            log.debug("不能获取用户的userId,uri:{}", request.getRequestURI());
        }

        for (Pattern pattern : excludeUrlPatterns) {
            Matcher m = pattern.matcher(request.getRequestURI());
            if (m.find()) {
                return;
            }
        }

        if (excludeUrls.contains(request.getRequestURI())) {
            return;
        }
        MDC.put(QUERY_STRING_KEY, RequestUtil.getQueryString(request));
        MDC.put(PARAMS_KEY, RequestUtil.getParameterString(request).toString());
    }
}
