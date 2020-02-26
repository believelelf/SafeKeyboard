package com.weiquding.safeKeyboard.common.exception;

import java.io.Serializable;

/**
 * 根据返回报文构建远程服务异常
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/17
 */
public class ResultFailException extends BaseRuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 接口返回数据
     */
    private Object result;

    /**
     * 接口异常
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @param result    接口返回数据
     * @param args      错误参数
     */
    public ResultFailException(String errorCode, String errorMsg, Object result, Object[] args) {
        super(errorCode, args, errorMsg);
        this.result = result;
    }

    /**
     * 接口异常
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @param result    接口返回数据
     */
    public ResultFailException(String errorCode, String errorMsg, Object result) {
        this(errorCode, errorMsg, result, null);
        this.result = result;
    }

    /**
     * 接口异常
     *
     * @param errorInfo 错误码
     * @param result    接口返回数据
     * @param args      错误参数
     */
    public ResultFailException(ErrorInfo errorInfo, Object result, Object[] args) {
        this(errorInfo.getCode(), errorInfo.getDefaultMsg(), result, args);
    }

    /**
     * 接口异常
     *
     * @param errorInfo 错误码
     * @param result    接口返回数据
     */
    public ResultFailException(ErrorInfo errorInfo, Object result) {
        this(errorInfo.getCode(), errorInfo.getDefaultMsg(), result);
    }

}
