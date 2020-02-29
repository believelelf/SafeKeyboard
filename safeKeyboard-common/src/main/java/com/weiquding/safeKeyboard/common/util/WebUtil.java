package com.weiquding.safeKeyboard.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Web工具类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/29
 */
public class WebUtil {

    private WebUtil() {
    }

    private static final ThreadLocal<HttpServletRequest> REQUEST_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletResponse> RESPONSE_THREAD_LOCAL = new ThreadLocal<>();

    public static HttpServletRequest getRequest() {
        return REQUEST_THREAD_LOCAL.get();
    }

    public static void setRequest(HttpServletRequest request) {
        REQUEST_THREAD_LOCAL.set(request);
    }

    public static void removeRequest() {
        REQUEST_THREAD_LOCAL.remove();
    }

    public static HttpServletResponse getResponse() {
        return RESPONSE_THREAD_LOCAL.get();
    }

    public static void setResponse(HttpServletResponse response) {
        RESPONSE_THREAD_LOCAL.set(response);
    }

    public static void removeResponse() {
        RESPONSE_THREAD_LOCAL.remove();
    }

}
