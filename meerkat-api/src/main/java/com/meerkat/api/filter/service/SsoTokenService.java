package com.meerkat.api.filter.service;

import com.meerkat.base.db.DB;
import com.meerkat.base.lang.Strings;
import com.meerkat.base.util.*;
import com.meerkat.entity.SsoToken;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wm on 17/4/18.
 */
@Service
public class SsoTokenService {

    private static final Logger logger = LoggerFactory.getLogger(SsoTokenService.class);
    private static final int SAVE_TO_DB_PERIOD = 10;
    private static final String SSO_TOKEN_CACHE_PREFIX = "sso:token:";
    private static CacheMap<String, SsoToken> ssoTokenCacheMap = new CacheMap(660000L, 500);
    private static ScheduledExecutorService ssoTokenSaveExe = Executors.newScheduledThreadPool(1);
    @Inject
    private DB db;

    private int timeoutMinutes = 30;//30分钟
    private int timeoutDay = 15;//15天
    private String cookieName = "token";
    private String cookieDomain;

    @PostConstruct
    public void init() {
        ssoTokenSaveExe.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Set<String> ssoTokenKeys = ssoTokenCacheMap.keySet();
                logger.info("此次共需保存{}个ssoToken", ssoTokenKeys.size());
                for (String token : ssoTokenKeys) {
                    SsoToken ssoToken = null;
                    try {
                        ssoToken = ssoTokenCacheMap.get(token);
                        logger.info("ssoToken:{}, expiryTime:{}", token, ssoToken.getExpiryTime());
                        db.update(ssoToken, "expiryTime", "terminal", "app_version", "appChannel", "updatedAt", "lastVisitIp", "deviceToken", "platformVersion", "deviceModel");
                    } catch (Exception e) {
                        String msg = "将内存里的token保存到数据库时出错:" + token + ", " + ssoToken;
                        logger.error(msg, e);
                    }
                }
            }
        }, 0, SAVE_TO_DB_PERIOD, TimeUnit.MINUTES);
    }

    public void setDb(DB db) {
        this.db = db;
    }

    public void setTimeoutMinutes(int timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public Cookie createCookie(SsoToken ssoToken, String host) {
        Cookie cookie = new Cookie(cookieName, ssoToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        setupCookieDomain(host, cookie);
        cookie.setMaxAge(timeoutMinutes * 60);
        return cookie;
    }

    public Cookie createCookie(String cookieValue, String host, int maxAge, String cName) {
        Cookie cookie = new Cookie(cName, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        setupCookieDomain(host, cookie);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    private void setupCookieDomain(String host, Cookie cookie) {
        if (Strings.isBlank(host)) {
            return;
        }
        host = host.toLowerCase();
        if (host.endsWith(".huoqiu.com")) {
            cookie.setDomain(".huoqiu.com");
        }
        /*else if (host.endsWith("stg1.huoqiu.cn") || host.endsWith("stg2.huoqiu.cn")) {
            cookie.setDomain(host);
        }*/
        else if (host.endsWith(".huoqiu.cn")) {
            cookie.setDomain(".huoqiu.cn");
        } else if (host.endsWith(".xip.io")) {
            cookie.setDomain(".xip.io");
        } else if (host.equals("localhost")) {
            cookie.setDomain("");
        }
    }

    public Cookie extractToken(HttpServletRequest request) {
        return WebContextUtil.extractToken(cookieName);
    }

    public void clearSsoToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie oldCookie = extractToken(request);
        if (oldCookie != null) {
            Cookie cookie = new Cookie(cookieName, "");
            String domain = request.getServerName();
            setupCookieDomain(domain, cookie);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }

    /**
     * 根据token字符串，获取ssoToken对象。获取顺序：本机内存（cachemap）->redis->db。
     * 如果本机内存或redis里不存在，在db查询到结果后将存入本机内存和redis，以便下次使用。
     *
     * @param token
     * @return
     */
    public SsoToken find(String token) {
        try {
            if (Strings.isBlank(token)) {
                return null;
            }
            //step 1
            SsoToken ssoToken = ssoTokenCacheMap.get(token);
            if (ssoToken != null) {
                return ssoToken;
            }
            //step 2
            ssoToken = getFromDB(token);
            if (ssoToken != null) {
                ssoTokenCacheMap.put(token, ssoToken);
                return ssoToken;
            }
        } catch (Exception e) {
            logger.error("获取ssoToekn时出错，token=" + token, e);
        }
        return null;
    }


    private SsoToken getFromDB(String token) {
        return db.from(SsoToken.class).where("token", token).great("expiry_time", new Date()).first(SsoToken.class);
    }

    private SsoToken getFromRedis(String token) {
        SsoToken ssoToken = null;
        String ssoTokenString = CacheUtil.getString(SSO_TOKEN_CACHE_PREFIX + token);
        if (StringUtils.isNotBlank(ssoTokenString)) {
            ssoToken = JsonUtil.load(ssoTokenString, SsoToken.class);
        }
        return ssoToken;
    }

    public Long getUserId(String token) {
        SsoToken ssoToken = find(token);
        if (ssoToken != null) {
            return ssoToken.getUserId();
        } else {
            return null;
        }
    }


    public SsoToken create(Long userId, Integer terminal) {
        return create(userId, terminal, null, null);
    }


    public SsoToken create(Long userId, int terminal, String version, String channel) {
        SsoToken ssoToken = new SsoToken();
        if (StringUtils.isNotBlank(version) || StringUtils.isBlank(channel)) {
            version = ThreadDebugInfo.get().getRequest().getHeader("version");
            channel = ThreadDebugInfo.get().getRequest().getHeader("channel");
            ssoToken.setDeviceToken(ThreadDebugInfo.get().getRequest().getHeader("device_token"));
            ssoToken.setLastVisitIp(ThreadDebugInfo.get().getRequest().getRemoteAddr());

            ClientOsInfo info = UAUtil.getMobilOS(WebContextUtil.getUA());
            if (info != null) {
                ssoToken.setPlatformVersion(info.getPlatformVersion());
                ssoToken.setDeviceModel(info.getDeviceModel());
            }
        }
        ssoToken.setUserId(userId);
        ssoToken.setAppVersion(version);
        ssoToken.setAppChannel(channel);
        if (StringUtils.isNotBlank(version) || StringUtils.isNotBlank(channel)) {
            ssoToken.setExpiryTime(DateTime.now().plusDays(timeoutDay).toDate());
        } else {
            ssoToken.setExpiryTime(DateTime.now().plusMinutes(timeoutMinutes).toDate());
        }

        ssoToken.setToken(UUIDUtil.getUUID());
        ssoToken.setUserId(userId);
        ssoToken.setTerminal(terminal);
        db.insert(ssoToken);

        return ssoToken;
    }


    public void extendExpireTime(SsoToken ssoToken, boolean fromApp) {
        if (fromApp) {
            ssoToken.setExpiryTime(DateTime.now().plusDays(timeoutDay).toDate());
        } else {
            ssoToken.setExpiryTime(DateTime.now().plusMinutes(timeoutMinutes).toDate());
        }
        ssoToken.setUpdatedAt(new Date());
        ssoTokenCacheMap.put(ssoToken.getToken(), ssoToken);
    }

    public void expireAllToken(Long userId, Long tokenId) {
        db.update("sso_token", "expiry_time", new Date(), "user_id=? and id<> ? and expiry_time>now()", userId, tokenId);
    }

    public String getCookieName() {
        return cookieName;
    }

    public String getCookieDomain() {
        return cookieDomain;
    }

}
