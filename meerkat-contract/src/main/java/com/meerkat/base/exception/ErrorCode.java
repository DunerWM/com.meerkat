package com.meerkat.base.exception;

/**
 * Created by wm on 17/5/7.
 */
public enum ErrorCode {

    FORCE_UPDATE(9999, "版本过低"),
    NEED_LOGIN(-100, "登录已失效"),
    INTERNAL_ERROR(-1, "系统内部错误"),
    VALIDATION_ERROR(-2, "数据校验错误"),
    BUSINESS_ERROR(-3, "肯定是出点了问题"),
    INVALID_ARGUMENT(1, "参数错误"),
    WRONG_CELL_NUMBER(2, "手机号码不正确"),
    CAPTCHA_FREQUENT(10004, "发送验证码过于频繁"),;

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public final int code;
    public final String message;

}
