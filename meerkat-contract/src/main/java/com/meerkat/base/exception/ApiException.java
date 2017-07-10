package com.meerkat.base.exception;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

/**
 * Created by wm on 17/5/7.
 */
public class ApiException extends RuntimeException {

    private final String EXCEPTION_TEXT = "Exception: ";
    private final ErrorCode errorCode;
    private Object[] messageArgs;

    public ApiException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCode errorCode, Object... messageArgs) {
        super(errorCode.message);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }


    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }


    public ApiException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ApiException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_ERROR;
    }

    public ApiException(String message, Throwable th) {
        super(message, th);
        this.errorCode = ErrorCode.BUSINESS_ERROR;
    }

    public ApiException(ErrorCode errorCode) {
        super(errorCode.message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (StringUtils.isNotBlank(message) && message.contains(EXCEPTION_TEXT)) {
            message = message.substring(message.lastIndexOf(EXCEPTION_TEXT) + EXCEPTION_TEXT.length());
        }
        if ((StringUtils.isBlank(message) || message.endsWith("Exception")) && errorCode != null) {
            message = errorCode.message;
        } else if (messageArgs != null && messageArgs.length > 0) {
            message = MessageFormat.format(message, messageArgs);
        }
        return StringUtils.isBlank(message) ? "服务器出了点问题" : message;
    }

    public static void main(String[] args) {
        ApiException apiException = new ApiException(ErrorCode.INTERNAL_ERROR, new ApiException(ErrorCode.BUSINESS_ERROR, "新浪返回内容错误"));
        System.out.println(apiException.getMessage());
    }

}
