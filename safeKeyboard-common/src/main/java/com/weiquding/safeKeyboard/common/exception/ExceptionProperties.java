package com.weiquding.safeKeyboard.common.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 异常属性类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/25
 * @see com.weiquding.safeKeyboard.common.exception.DefaultExceptionHandler
 */
@Data
@ConfigurationProperties(prefix = "base.exception")
public class ExceptionProperties {

    /**
     * 默认错误码
     */
    private String defaultMappingCode = "BASEBP0001";
    /**
     * 重新错误码
     */
    private boolean rebuildMessageCode = true;
    /**
     * 是否输出HostName信息
     */
    private boolean outputHostName = false;
    /**
     * 错误码映射
     */
    private Map<String, String> messageCodeMapping;



}
