package com.meerkat.api.filter;

import com.meerkat.base.util.ApplicationContextProvider;
import com.meerkat.entity.User;
import com.meerkat.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wm on 17/4/18.
 */
public class RetrieveUserFilter extends BaseFilter {

    //用户token异常导致失效后，最大尝试获取次数
    private static int MAX_TIMES = 3;

    private Logger logger = LoggerFactory.getLogger(RetrieveUserFilter.class);


    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        Long userId = (Long) request.getSession(true).getAttribute("userId");
        if (userId != null) {
            Object user = request.getSession().getAttribute("user");
            if (user != null && userId.equals(((User) user).getId())) {
                request.setAttribute("user", user);
                request.setAttribute("userId", userId);
                MDC.put("retrieve-user", "从Session里获取user实体");
            } else {
                // 异常之后重试Max_Times次
                for (int time = 0; time < MAX_TIMES; time++) {
                    try {
                        IUserService userService = ApplicationContextProvider.getApplicationContext().getBean(IUserService.class);
                        user = userService.getById(userId);
                        break;
                    } catch (Exception e) {
                        time++;
                        if (time == MAX_TIMES) {
                            logger.error("已经尝试多次获取token失败 userId:" + userId + "", e);
                            MDC.put("retrieve-user", "调用gaea" + MAX_TIMES + "次后仍失败");
                        }
                    }
                }
                if (user != null) {
                    request.setAttribute("user", user);
                    request.getSession().setAttribute("user", user);
                } else {
                    request.getSession().removeAttribute("userId");
                    request.getSession().removeAttribute("user");
                    request.removeAttribute("userId");
                    request.removeAttribute("user");
                }
            }
        }
        chain.doFilter(request, response);
    }

}
