package com.meerkat.base.exception;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wm on 17/5/7.
 */
public class ExceptionHandler extends AbstractHandlerMethodExceptionResolver {

    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
        return ExceptionUtil.handleException(this, request, response, handlerMethod, exception);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
