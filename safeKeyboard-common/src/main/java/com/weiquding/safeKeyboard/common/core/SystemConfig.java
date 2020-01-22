package com.weiquding.safeKeyboard.common.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.net.InetAddress;

/**
 * 系统属性配置
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/22
 */
@Slf4j
@Data
public class SystemConfig implements InitializingBean {

    /**
     * 系统编码
     */
    private String systemCode;
    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 主机名称
     */
    private String hostName = "LocalHost";

    /**
     * 主机地址
     */
    private String hostAddress = "127.0.0.1";


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.systemCode, "systemCode is required, 8 bits number.");
        if (this.systemName == null) {
            this.systemName = this.systemCode;
        }
        InetAddress localInetAddress = InetAddress.getLocalHost();
        this.hostName = localInetAddress.getHostName();
        this.hostAddress = localInetAddress.getHostAddress();
        MDC.put("hostName", this.hostName);
        MDC.put("hostAddress", this.hostAddress);
    }
}
