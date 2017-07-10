package com.meerkat.api.filter;

import org.slf4j.MDC;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wm on 17/5/7.
 */
public class ClearMDCFilter extends BaseFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        MDC.clear();
        chain.doFilter(request, response);
    }

}
