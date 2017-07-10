package com.meerkat.base.util;

import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wm on 17/4/18.
 */
public class DebugInfo {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DebugInfo.class);

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String threadName;
    private boolean isDebug;
    private boolean isFromDebug;
    private boolean isTrace;
    private boolean isFromTrace;
    private long lastTraceTime;
    private int traceCount = 0;
    private Map<String, String> traceRecord = new LinkedHashMap<>();
    private Map<String, Object> paramsMap = new LinkedHashMap<>();


    private static final String TRACE_HEADER_PREFIX = "trace-";

    public void addTrace(String key) {
        long currentTime = System.currentTimeMillis();
        String timeCost = String.valueOf(currentTime - lastTraceTime);
        lastTraceTime = currentTime;
        String headerKey = TRACE_HEADER_PREFIX + (traceCount++) + "-" + key;
        String headerValue = currentTime + "," + timeCost;
        log.info(headerKey + "=" + headerValue);
        if (isTrace || isFromTrace) {
            response.addHeader(headerKey, headerValue);
        }

    }

    public boolean isFromDebug() {
        return isFromDebug;
    }

    public void setFromDebug(boolean fromDebug) {
        isFromDebug = fromDebug;
    }

    public boolean isFromTrace() {
        return isFromTrace;
    }

    public void setFromTrace(boolean fromTrace) {
        isFromTrace = fromTrace;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public boolean isTrace() {
        return isTrace;
    }

    public void setTrace(boolean trace) {
        isTrace = trace;
    }

    public long getLastTraceTime() {
        return lastTraceTime;
    }

    public void setLastTraceTime(long lastTraceTime) {
        this.lastTraceTime = lastTraceTime;
    }

    public Map<String, String> getTraceRecord() {
        return traceRecord;
    }

    public void setTraceRecord(Map<String, String> traceRecord) {
        this.traceRecord = traceRecord;
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public String toString() {
        return "DebugInfo{" +
                "request=" + request +
                ", response=" + response +
                ", threadName='" + threadName + '\'' +
                ", isDebug=" + isDebug +
                ", isFromDebug=" + isFromDebug +
                ", isTrace=" + isTrace +
                ", isFromTrace=" + isFromTrace +
                ", lastTraceTime=" + lastTraceTime +
                ", traceCount=" + traceCount +
                ", traceRecord=" + traceRecord +
                ", paramsMap=" + paramsMap +
                '}';
    }

}
