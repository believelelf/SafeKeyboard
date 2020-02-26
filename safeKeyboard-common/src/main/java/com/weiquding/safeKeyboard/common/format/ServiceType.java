package com.weiquding.safeKeyboard.common.format;

import com.weiquding.safeKeyboard.common.exception.SKBS0001ResultFailException;

/**
 * 系统渠道
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/26
 */
public enum ServiceType {

    /**
     * 安全服务系统
     */
    SKBS0001("SKBS0001ExchangeHandler", SKBS0001ResultFailException.class);

    /**
     * 处理器
     */
    private String exchangeHandler;
    /**
     * 对应异常类
     */
    private Class resultFailException;

    ServiceType(String exchangeHandler, Class resultFailException) {
        this.exchangeHandler = exchangeHandler;
        this.resultFailException = resultFailException;
    }

    public String getExchangeHandler() {
        return exchangeHandler;
    }

    public Class getResultFailException() {
        return resultFailException;
    }

}