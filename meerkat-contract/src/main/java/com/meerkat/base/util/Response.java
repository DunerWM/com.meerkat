package com.meerkat.base.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Created by wm on 17/3/9.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private int code;
    private boolean success;
    private String message;
    private Map<String, Object> data;

    public Response() {
    }

    public Response(JsonResponse jsonResponse) {
        if (jsonResponse == null) {
            success = false;
        } else {
            success = jsonResponse.isSuccess();
            message = jsonResponse.getMessage();
            data = jsonResponse.getData();
        }
    }


    public Response(boolean success) {
        this.success = success;
    }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Response(boolean success, String message, Map<String, Object> data) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}