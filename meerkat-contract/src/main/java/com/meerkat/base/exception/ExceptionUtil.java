package com.meerkat.base.exception;

import com.google.common.base.Charsets;
import com.meerkat.base.lang.Maps;
import com.meerkat.base.util.ConfigPropertiesUtil;
import com.meerkat.base.util.JsonResponse;
import com.meerkat.base.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;

/**
 * Created by wm on 17/5/7.
 */
public class ExceptionUtil {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ExceptionUtil.class);
    public static final String HAS_SENT_ERROR_MAIL = "has_sent_error_mail";
    public static final String CONTENT_TYPE = "application/json; charset=UTF-8";
    private static String DEFAULT_MSG = StringUtils.isBlank(ConfigPropertiesUtil.getValue("error.message.default")) ? "肯定是什么地方出了点问题" : ConfigPropertiesUtil.getValue("error.message.text");

    public static ModelAndView handleException(Object location, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
        boolean hasSent = request.getAttribute(HAS_SENT_ERROR_MAIL) != null;
        if (hasSent) {
            //only one email per exception but ApiException
        } else {
            //log and mail
            String msg = "[" + StringUtils.abbreviate(exception.getMessage(), 10) + "] occur an error while handle [" + request.getMethod() + ", " + request.getRequestURL() + "]";
            try {
                Throwable logException = exception;
                if (logException instanceof UndeclaredThrowableException && logException.getCause() != null) {
                    logException = logException.getCause();
                }
                if (logException.getClass().getName().equals("com.bj58.spat.gaea.protocol.exception.ServiceException") && logException.getCause() != null) {
                    logException = logException.getCause();
                }

                StringBuilder logMsg = new StringBuilder("[");
                logMsg.append(logException.getClass().getSimpleName());
                if (StringUtils.isNotBlank(logException.getMessage()) && logException.getMessage().length() <= 10) {
                    logMsg.append(": ");
                    logMsg.append(logException.getMessage());
                }
                msg = StringUtils.abbreviate(logMsg.toString(), 100) + "] error";
            } catch (Exception e) {
                log.error("发送异常邮件时出现问题", e);
//                PostManUtil.sendErrorMail("发送异常邮件时出现问题", e);
            }

            if (!(exception instanceof ApiException) || ConfigPropertiesUtil.getBoolean("api.exception.log")) {
                log.error(msg, exception);
            }

            if (!(exception instanceof ApiException) || ConfigPropertiesUtil.getBoolean("api.exception.mail")) {
                log.error(msg, exception);
//                PostManUtil.sendErrorMail(msg, exception);
            }

            request.setAttribute(HAS_SENT_ERROR_MAIL, true);
//            request.setAttribute(AccessLog.STATUS_KEY, 500);
//            request.setAttribute(AccessLog.EXCEPTION_KEY, exception);
        }


        String message = exception.getMessage();
        int code = ErrorCode.BUSINESS_ERROR.code;
        if (exception instanceof ApiException) {
            ApiException e = (ApiException) exception;
            message = e.getMessage();
            code = e.getErrorCode().code;
        } else if (exception instanceof BindException) {
            message = ((BindException) exception).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }
        log.error("exception message:{}", message);

        if (handlerMethod != null && (handlerMethod.getMethodAnnotation(ResponseBody.class) != null ||
                handlerMethod.getBeanType().getAnnotation(ResponseBody.class) != null)) {
            message = StringUtils.isBlank(message) || StringUtils.isAsciiPrintable(message) ? DEFAULT_MSG : message;
            message = message.length() > 15 ? DEFAULT_MSG : message;
            dumpError(response, new JsonResponse(message).setCode(code));
            return new ModelAndView();
        } else {
            log.error("occur an error，return 500 view to client");
            return new ModelAndView("500");
        }
    }

    private static void dumpError(ServletResponse response, String message) {
        Map<String, ?> result = Maps.newHashMap("success", false, "message", message);
        dumpError(response, result);
    }

    private static void dumpError(ServletResponse response, JsonResponse jsonResponse) {
        dumpString(response, JsonUtil.dump(jsonResponse));
    }

    private static void dumpError(ServletResponse response, Map<String, ?> result) {
        dumpString(response, JsonUtil.dump(result));
    }

    private static void dumpString(ServletResponse response, String string) {
        Logger logger = LoggerFactory.getLogger("org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor");
        logger.debug("Written {} as {} using {}", string, CONTENT_TYPE, ExceptionUtil.class.getName());
        try {
            response.setContentType(CONTENT_TYPE);
            ServletOutputStream out = response.getOutputStream();
            out.write(string.getBytes(Charsets.UTF_8));
            out.flush();
        } catch (Throwable throwable) {
            //ignore
        }
    }

}
