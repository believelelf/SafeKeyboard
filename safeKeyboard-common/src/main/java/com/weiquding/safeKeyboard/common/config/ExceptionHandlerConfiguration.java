package com.weiquding.safeKeyboard.common.config;

import com.weiquding.safeKeyboard.common.core.MessagesProvider;
import com.weiquding.safeKeyboard.common.core.SystemConfig;
import com.weiquding.safeKeyboard.common.exception.DefaultExceptionHandler;
import com.weiquding.safeKeyboard.common.exception.ExceptionHandler;
import com.weiquding.safeKeyboard.common.exception.ExceptionProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/25
 */
@Configuration
@EnableConfigurationProperties(ExceptionProperties.class)
public class ExceptionHandlerConfiguration {

    @Bean
    @ConditionalOnMissingBean(ExceptionHandler.class)
    public ExceptionHandler exceptionHandler(MessagesProvider messagesProvider, SystemConfig systemConfig, ExceptionProperties exceptionProperties) {
        DefaultExceptionHandler exceptionHandler = new DefaultExceptionHandler();
        exceptionHandler.setMessagesProvider(messagesProvider);
        exceptionHandler.setSystemConfig(systemConfig);
        exceptionHandler.setRebuildMessageCode(exceptionProperties.isRebuildMessageCode());
        exceptionHandler.setMessageCodeMapping(exceptionProperties.getMessageCodeMapping());
        exceptionHandler.setOutputHostName(exceptionProperties.isOutputHostName());
        exceptionHandler.setDefaultMappingCode(exceptionProperties.getDefaultMappingCode());
        return exceptionHandler;
    }
}
