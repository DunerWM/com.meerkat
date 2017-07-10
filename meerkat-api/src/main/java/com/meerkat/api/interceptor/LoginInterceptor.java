package com.meerkat.api.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by Friedemann Lee on 2017-03-26 11:16.
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        //如果是商家后台端接口，拦截校验
//        if(path.indexOf("business")>=0){
            if (request.getSession().getAttribute("business-login") == null){
                PrintWriter out = response.getWriter();
                out.print("{\"code\":-1,\"message\":\"身份校验失败\"}");
                out.close();
                return false;
            }
//        }
        return true;
    }
}
