package com.weiquding.safeKeyboard.common.interceptor;

import com.weiquding.safeKeyboard.common.util.WebUtil;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 本地线程缓存Request and Response
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/29
 */
public class LocalCacheInterceptor extends HandlerInterceptorAdapter {

    /**
     * This implementation always returns {@code true}.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        WebUtil.setRequest(request);
        WebUtil.setResponse(response);
        return true;
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        WebUtil.removeRequest();
        WebUtil.removeResponse();
    }
}
