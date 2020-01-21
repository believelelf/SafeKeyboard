package com.weiquding.safeKeyboard.common.exception;

import org.springframework.context.MessageSourceResolvable;

import java.lang.reflect.InvocationTargetException;

/**
 * 异常工具类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/20
 */
public class ExceptionUtil {

    private ExceptionUtil() {
    }

    /**
     * 是否为包装类异常
     *
     * @param th Throwable
     * @return 是否为包装类异常
     */
    @SuppressWarnings("ALL")
    public static boolean isWrapped(Throwable th) {
        if (th == null) {
            return false;
        }
        if (th instanceof InvocationTargetException) {
            return true;
        }
        if (!(th instanceof MessageSourceResolvable)) {
            if (th.getCause() instanceof MessageSourceResolvable) {
                return true;
            }
        }
        return false;
    }
}