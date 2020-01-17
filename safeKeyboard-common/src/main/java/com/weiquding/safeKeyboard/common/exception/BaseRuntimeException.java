package com.weiquding.safeKeyboard.common.exception;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * 基类异常
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/17
 */
public class BaseRuntimeException extends RuntimeException implements MessageSourceResolvable {

    private static final long serialVersionUID = -3617744653279475003L;

    private final DefaultMessageSourceResolvable messageSourceResolvable;

    private boolean alreadyLogged;

    public boolean isAlreadyLogged() {
        return this.alreadyLogged;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        this.alreadyLogged = true;
        return super.getStackTrace();
    }

    public BaseRuntimeException(String code) {
        super(joinMessage(code, null));
        this.messageSourceResolvable = new DefaultMessageSourceResolvable(code);
    }

    public BaseRuntimeException(String code, Object[] args) {
        super(joinMessage(code, null));
        this.messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args);
    }

    public BaseRuntimeException(String code, Throwable th) {
        super(joinMessage(code, null), th);
        if (th instanceof MessageSourceResolvable) {
            this.messageSourceResolvable = new DefaultMessageSourceResolvable((MessageSourceResolvable) th);
        } else {
            this.messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, null, th.getMessage());

        }
    }

    public BaseRuntimeException(String code, Object[] args, Throwable th) {
        super(joinMessage(code, null), th);
        if (th instanceof MessageSourceResolvable) {
            this.messageSourceResolvable = new DefaultMessageSourceResolvable((MessageSourceResolvable) th);
        } else {
            this.messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args, th.getMessage());
        }
    }

    public BaseRuntimeException(String code, String defaultMsg) {
        super(joinMessage(code, defaultMsg));
        this.messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, null, defaultMsg);
    }

    public BaseRuntimeException(String code, Object[] args, String defaultMsg) {
        super(joinMessage(code, defaultMsg));
        this.messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args, defaultMsg);
    }

    public BaseRuntimeException(String code, String defaultMsg, Throwable th) {
        super(joinMessage(code, defaultMsg), th);
        if (th instanceof MessageSourceResolvable) {
            this.messageSourceResolvable = new DefaultMessageSourceResolvable((MessageSourceResolvable) th);
        } else {
            this.messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, new Object[]{th.getClass().getName()}, defaultMsg);
        }
    }

    public BaseRuntimeException(String code, Object[] args, String defaultMsg, Throwable th) {
        super(joinMessage(code, defaultMsg), th);
        if (th instanceof MessageSourceResolvable) {
            this.messageSourceResolvable = new DefaultMessageSourceResolvable((MessageSourceResolvable) th);
        } else {
            this.messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args, defaultMsg);
        }
    }

    /**
     * 拼接错误信息
     *
     * @param code       错误码
     * @param defaultMsg 默认错误信息
     * @return 错误信息
     */
    private static String joinMessage(String code, String defaultMsg) {
        if (code == null) {
            return defaultMsg;
        }
        if (defaultMsg == null) {
            return "[" + code + "]";
        }
        return "[" + code + "]" + defaultMsg;
    }

    public String getCode() {
        return this.messageSourceResolvable.getCode();
    }

    @Override
    public String[] getCodes() {
        return this.messageSourceResolvable.getCodes();
    }

    @Override
    public Object[] getArguments() {
        return this.messageSourceResolvable.getArguments();
    }

    @Override
    public String getDefaultMessage() {
        return this.messageSourceResolvable.getDefaultMessage();
    }
}
