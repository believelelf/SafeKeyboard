package com.weiquding.safeKeyboard.common.exception;

import com.weiquding.safeKeyboard.common.format.ServiceType;

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
     * 系统渠道
     */
    private ServiceType serviceType;

    /**
     * 接口异常
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @param result    接口返回数据
     * @param args      错误参数
     */
    public ResultFailException(ServiceType serviceType, String errorCode, String errorMsg, Object result, Object[] args) {
        super(serviceType.isPrepend() ? serviceType.getSystemCode() + errorCode : errorCode, args, errorMsg);
        this.result = result;
        this.serviceType = serviceType;
    }

    /**
     * 接口异常
     *
     * @param serviceType 系统渠道
     * @param errorCode   错误码
     * @param errorMsg    错误信息
     * @param result      接口返回数据
     */
    public ResultFailException(ServiceType serviceType, String errorCode, String errorMsg, Object result) {
        this(serviceType, errorCode, errorMsg, result, null);
    }


    /**
     * 接口异常
     *
     * @param serviceType 系统渠道
     * @param errorInfo   错误码
     * @param result      接口返回数据
     * @param args        错误参数
     */
    public ResultFailException(ServiceType serviceType, ErrorInfo errorInfo, Object result, Object[] args) {
        this(serviceType, errorInfo.getCode(), errorInfo.getDefaultMsg(), result, args);
    }

    /**
     * 接口异常
     *
     * @param serviceType 系统渠道
     * @param errorInfo   错误码
     * @param result      接口返回数据
     */
    public ResultFailException(ServiceType serviceType, ErrorInfo errorInfo, Object result) {
        this(serviceType, errorInfo.getCode(), errorInfo.getDefaultMsg(), result);
    }

}
