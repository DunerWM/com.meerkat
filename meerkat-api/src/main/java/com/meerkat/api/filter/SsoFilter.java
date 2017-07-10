package com.meerkat.api.filter;

import com.meerkat.api.filter.service.SsoTokenService;
import com.meerkat.base.lang.Strings;
import com.meerkat.base.util.*;
import com.meerkat.entity.SsoToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wm on 17/4/18.
 */
public class SsoFilter extends BaseFilter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SsoFilter.class);
    private Pattern ignoreUrlsPattern;
    private boolean onlyOnePointLogin = false;

    public void init(FilterConfig filterConfig) throws ServletException {
        String ignoreUrlsParam = filterConfig.getInitParameter("ignoreUrls");
        if (!Strings.isBlank(ignoreUrlsParam)) {
            ignoreUrlsPattern = Pattern.compile(ignoreUrlsParam);
        }
        onlyOnePointLogin = ConfigPropertiesUtil.getBoolean("sso.token.one.point.login");
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        SsoTokenService ssoTokenService = context.getBean(SsoTokenService.class);
        String token = request.getHeader(ssoTokenService.getCookieName());
        if (token == null) {
            Cookie cookie = ssoTokenService.extractToken(request);
            token = cookie == null ? token : cookie.getValue();
        }
        if (StringUtils.isNotBlank(token)) {
            SsoToken ssoToken = ssoTokenService.find(token);
            HttpSession session = request.getSession(true);

            if (ssoToken == null) {
                MDC.put("sso-token", "找不到对应的ssoToken对象");
                logger.warn("根据token\"{}\"找不到对应的ssoToken对象", token);
            } else if (ssoToken.isExpired()) {
                MDC.put("sso-token", "token已在" + DateUtil.DateToString(ssoToken.getExpiryTime()) + "过期");
                logger.warn("根据token\"{}\",发现token已过期:{}", token, ssoToken);
            } else {
                // 根据配置判断是否将其他token标记为失效状态,以满足单一设备登录的需求。
                if (onlyOnePointLogin) {
                    ssoTokenService.expireAllToken(ssoToken.getUserId(), ssoToken.getId());
                }
                ClientOsInfo info = UAUtil.getMobilOS(WebContextUtil.getUA());
                if (info != null) {
                    ssoToken.setPlatformVersion(info.getPlatformVersion());
                    ssoToken.setDeviceModel(info.getDeviceModel());
                }
                ssoToken.setAppVersion(request.getHeader("version"));
                ssoToken.setAppChannel(request.getHeader("channel"));
                ssoToken.setDeviceToken(request.getHeader("device_token"));
                ssoToken.setTerminal(WebContextUtil.getTerminal().index);
                ssoToken.setLastVisitIp(request.getRemoteAddr());
                session.setAttribute("userId", ssoToken.getUserId());
                request.setAttribute("userId", ssoToken.getUserId());
                ssoTokenService.extendExpireTime(ssoToken, WebContextUtil.isApp());
            }
        }
        addDebugUser(request);
        chain.doFilter(request, response);
    }

    private void addDebugUser(HttpServletRequest request) {
        if (request.getSession().getAttribute("userId") == null && request.getAttribute("userId") == null && ConfigPropertiesUtil.getBoolean("site.add.default.user")) {
            Long debugUserId = ConfigPropertiesUtil.getLong("site.add.default.user.id", Long.MAX_VALUE);
            logger.info("add debugUserId as {}", debugUserId);
            request.getSession().setAttribute("userId", debugUserId);
            request.setAttribute("userId", debugUserId);
        }
    }

    private boolean shouldIgnore(HttpServletRequest request) {
        if (ignoreUrlsPattern == null) {
            return false;
        }
        Matcher m = ignoreUrlsPattern.matcher(request.getRequestURI());
        return m.find();
    }
}
