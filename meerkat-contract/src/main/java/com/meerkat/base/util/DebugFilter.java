package com.meerkat.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wm on 17/4/27.
 */
public class DebugFilter implements Filter{

    static Logger log = LoggerFactory.getLogger(DebugFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ThreadDebugInfo.remove();
        DebugInfo debugInfo = new DebugInfo();
        debugInfo.setDebug(MotiDebug.isDebug(request));
        debugInfo.setTrace(MotiDebug.isTrace(request));
        debugInfo.setFromDebug(MotiDebug.isFromDebug(request));
        debugInfo.setFromTrace(MotiDebug.isFromTrace(request));
        debugInfo.setLastTraceTime(System.currentTimeMillis());
        debugInfo.setRequest(request);
        debugInfo.setResponse(response);
        debugInfo.setThreadName(Thread.currentThread().getName());

        ThreadDebugInfo.set(debugInfo);
        chain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void destroy() {

    }

}
