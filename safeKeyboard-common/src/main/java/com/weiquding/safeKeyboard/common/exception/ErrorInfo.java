package com.weiquding.safeKeyboard.common.exception;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 错误信息
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/16
 */
public class ErrorInfo {

    private static final Map<String, String> ERROR_DEFINED = new ConcurrentHashMap<>();

    /**
     * 错误码
     */
    private String code;
    /**
     * 错误信息
     */
    private String msg;

    /**
     * 私有构造
     *
     * @param code 错误码
     * @param msg  错误信息
     */
    private ErrorInfo(String code, String msg) {
        Assert.isNull(ERROR_DEFINED.get(code), "Error code [" + code + "] repeats definition");
        this.code = code;
        this.msg = msg;
        ERROR_DEFINED.put(code, msg);
    }

    /**
     * 定义一个错误信息
     *
     * @param code       错误码
     * @param defaultMsg 默认错误信息
     * @return ErrorInfo
     */
    public static ErrorInfo item(String code, String defaultMsg) {
        return new ErrorInfo(code, defaultMsg);
    }

    /**
     * 抛出异常
     *
     * @return TranFailException 交易异常
     */
    public TranFailException initialize() {
        return new TranFailException(this);
    }

    /**
     * 抛出异常
     *
     * @param args 交易参数
     * @return TranFailException 交易异常
     */
    public TranFailException initialize(Object... args) {
        return new TranFailException(this, args);
    }

    /**
     * 抛出异常
     *
     * @param th 原异常
     * @return TranFailException 交易异常
     */
    public TranFailException initialize(Throwable th) {
        return new TranFailException(this, th);
    }

    /**
     * 抛出异常
     *
     * @param th   原异常
     * @param args 交易参数
     * @return TranFailException 交易异常
     */
    public TranFailException initialize(Throwable th, Object... args) {
        return new TranFailException(this, th, args);
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取默认错误信息
     *
     * @return 默认错误信息
     */
    public String getDefaultMsg() {
        return msg;
    }


}
