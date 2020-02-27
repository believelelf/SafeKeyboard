package com.weiquding.safeKeyboard.common.format;


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
    SKBS0001("SKBS0001", false);

    /**
     * 系统代码
     */
    private String systemCode;
    /**
     * 是否在错误码前追加系统代码
     */
    private boolean prepend;

    ServiceType(String systemCode, boolean prepend) {
        this.systemCode = systemCode;
        this.prepend = prepend;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public boolean isPrepend() {
        return prepend;
    }

}