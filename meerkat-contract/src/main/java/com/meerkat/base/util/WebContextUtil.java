package com.meerkat.base.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wm on 16/11/2.
 */
public class WebContextUtil {

    private static final Logger logger = LoggerFactory.getLogger(WebContextUtil.class);
    public static final String DEFAULT_REMOTE_IP = "0.0.0.0";
    public static final String DEFAULT_LOCAL_IP = "127.0.0.1";

    public static boolean isOlderOrEqualThan(int androidVerion, int iosVersion) {
        return (WebContextUtil.isAndroid() && WebContextUtil.getVersion() <= androidVerion) || (WebContextUtil.isIos() && WebContextUtil.getVersion() <= iosVersion);
    }

    public static boolean isOlderThan(int androidVerion, int iosVersion) {
        return (WebContextUtil.isAndroid() && WebContextUtil.getVersion() < androidVerion) || (WebContextUtil.isIos() && WebContextUtil.getVersion() < iosVersion);
    }

    public static boolean isNewerOrEqualThan(int androidVerion, int iosVersion) {
        return (WebContextUtil.isAndroid() && WebContextUtil.getVersion() >= androidVerion) || (WebContextUtil.isIos() && WebContextUtil.getVersion() >= iosVersion);
    }

    public static boolean isNewerThan(int androidVerion, int iosVersion) {
        return (WebContextUtil.isAndroid() && WebContextUtil.getVersion() > androidVerion) || (WebContextUtil.isIos() && WebContextUtil.getVersion() > iosVersion);
    }

    public static boolean isApp() {
        return getVersion() > 0 || StringUtils.isNotBlank(getPlatform());
    }

    public static boolean isMobile() {
        return isAndroid() || isIos() || isWindowsPhone();
    }

    public static int getTerminalNumber() {
        return getTerminal().index;
    }


    public static boolean isAndroid() {
        return getTerminal() == Terminal.ANDROID;
    }

    public static boolean isIos() {
        return getTerminal() == Terminal.IOS;
    }

    public static boolean isWindowsPhone() {
        return getTerminal() == Terminal.WINPHONE;
    }

    public static boolean isWeChat() {
        String ua = getUA();
        return StringUtils.isNotBlank(ua) && ua.toUpperCase().contains("MICROMESSENGER");
    }

    public static String getUA() {
        return getRequest().getHeader("User-Agent");
    }

    public static int getVersion() {
        String version = getRequest().getHeader("version");

        if (StringUtils.isEmpty(version) || !NumberUtils.isDigits(version)) {
            logger.warn("version{} isn't a number", version);
            return 0;
        }

        return Integer.parseInt(version);
    }

    public static String getPlatform() {
        String platform = getRequest().getHeader("platform");
        if (StringUtils.isEmpty(platform)) {
            platform = "";
        }
        return platform.toLowerCase();
    }

    public static Terminal getTerminal() {
        String platform = getPlatform();
        if (StringUtils.isBlank(platform)) {
            Terminal terminal = Terminal.UNKNOWN;
            String ua = getUA();
            if (StringUtils.isNotBlank(ua)) {
                ua = ua.toUpperCase();
                if (ua.contains(Terminal.ANDROID.name)) {
                    terminal = Terminal.ANDROID;
                } else if (ua.contains(Terminal.IOS.name) || ua.contains("IPHONE") || ua.contains("IPAD") || ua.contains("IPOD")) {
                    terminal = Terminal.IOS;
                } else if (ua.contains("WINDOWS PHONE")) {
                    terminal = Terminal.WINPHONE;
                } else if (ua.contains("TRIDENT") || ua.contains("APPLEWEBKIT") || ua.contains("GECKO") || ua.contains("PRESTO")) {
                    terminal = Terminal.PC;
                }
            }
            return terminal;
        }
        return Terminal.parseFromName(platform.toUpperCase());
    }


    public static String getRemoteAddr() {
        HttpServletRequest request = getRequest();
        if (null == request) {
            return DEFAULT_REMOTE_IP;
        }
        String ip = request.getRemoteAddr();
        return ip;
    }


    public static String getDomainWithPort() {
        HttpServletRequest request = getRequest();
        StringBuffer url = new StringBuffer(request.getScheme()).append("://").append(request.getServerName());
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            url.append(":").append(request.getServerPort());
        }
        return url.toString();
    }

    public static StringBuffer getRequestUrl() {
        StringBuffer url = new StringBuffer(getDomainWithPort());
        HttpServletRequest request = getRequest();
        url.append(request.getRequestURI());
        if (StringUtils.isNotBlank(request.getQueryString())) {
            url.append("?").append(request.getQueryString());
        }
        logger.info("request url: " + url.toString());
        return url;
    }

    public static Cookie extractToken(String cookieName) {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static HttpServletRequest getRequest() {
        DebugInfo debugInfo = ThreadDebugInfo.get();
        if (null == debugInfo) {
            return null;
        }
        HttpServletRequest request = debugInfo.getRequest();
        return request;
    }


    public enum Terminal {
        UNKNOWN(-1, 50, "UNKNOWN"),
        PC(0, 10, "PC"),
        WAP(1, 40, "WAP"),
        ANDROID(2, 20, "ANDROID"),
        IOS(3, 30, "IOS"),
        WINPHONE(4, 40, "WINPHONE");

        private static final Map<String, Terminal> map = new HashMap<>();

        static {
            for (Terminal terminal : Terminal.values()) {
                map.put(terminal.name, terminal);
            }
        }

        public int index;
        public int ssoIndex;
        public String name;

        Terminal(int index, int ssoIndex, String name) {
            this.index = index;
            this.ssoIndex = ssoIndex;
            this.name = name;
        }

        public static Terminal parseFromName(String name) {
            Terminal terminal = map.get(name);
            return terminal == null ? Terminal.UNKNOWN : terminal;
        }
    }

    /**
     * 返回一个良好的可访问的url
     *
     * @param redirectUrl 一个url ,可能是 /mobile/what/do | http://www.baidu.com | https://www.houqiu.cn/dd/do
     * @return
     */
    public static String getNiceUrl(String redirectUrl) {
        if (StringUtils.isNotBlank(redirectUrl)) {
            if (!redirectUrl.toLowerCase().startsWith("http")) {
                if (!redirectUrl.startsWith("/")) {
                    redirectUrl = "/" + redirectUrl;
                }
                DebugInfo debugInfo = ThreadDebugInfo.get();
                if (debugInfo != null && debugInfo.getRequest() != null) {
                    redirectUrl = WebContextUtil.getDomainWithPort() + redirectUrl;
                }
            }
        }
        return redirectUrl;
    }

}
