package com.weiquding.safeKeyboard.common.config;

import com.weiquding.safeKeyboard.common.core.DefaultMessagesProvider;
import com.weiquding.safeKeyboard.common.core.MessagesProvider;
import com.weiquding.safeKeyboard.common.core.SystemConfig;
import com.weiquding.safeKeyboard.common.mapper.MessagesMapper;
import com.weiquding.safeKeyboard.common.support.DefaultDatabaseMessageLoader;
import com.weiquding.safeKeyboard.common.support.ReloadableDatabaseMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * 区分装配{@link ReloadableResourceBundleMessageSource} 和 {@link ReloadableDatabaseMessageSource}
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/24
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME, search = SearchStrategy.CURRENT)
@EnableConfigurationProperties
public class MessageSourceConfiguration {

    @Value("${base.messages.default-mapping-code:BASEBP0001}")
    private String defaultMappingCode;

    @Bean
    @ConfigurationProperties(prefix = "base.messages.values-bundles")
    public MessageSourceProperties valuesBundlesProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "base.messages.errors-bundles")
    public MessageSourceProperties errorsBundlesProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "base.messages.values-tables")
    public MessageSourceProperties valuesTablesProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "base.messages.errors-tables")
    public MessageSourceProperties errorsTablesProperties() {
        return new MessageSourceProperties();
    }


    @Bean(name = {"systemConfig"})
    @ConfigurationProperties(prefix = "base.config", ignoreUnknownFields = true)
    public SystemConfig systemConfig() {
        return new SystemConfig();
    }


    private MessageSource messageSource(AbstractResourceBasedMessageSource messageSource, MessageSourceProperties properties) {
        if (StringUtils.hasText(properties.getBasename())) {
            messageSource.setBasenames(StringUtils
                    .commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename())));
        }
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }


    @Primary
    @Bean
    @ConditionalOnMissingBean(MessagesProvider.class)
    public MessagesProvider messagesProvider(
            @Qualifier("defaultMessageSource") MessageSource defaultMessageSource,
            @Qualifier("errorMessageSource") MessageSource errorMessageSource
    ) {
        DefaultMessagesProvider messagesProvider = new DefaultMessagesProvider();
        messagesProvider.setDefaultMappingCode(defaultMappingCode);
        messagesProvider.setDefaultMessageSource(defaultMessageSource);
        messagesProvider.setErrorMessageSource(errorMessageSource);
        return messagesProvider;
    }

    /**
     * 装置ResourceBundle资源
     */
    @Configuration
    @ConditionalOnProperty(name = "base.messages.type", havingValue = "bundle", matchIfMissing = true)
    public class ReloadableResourceBundleMessageSourceConfiguration {

        @Primary
        @Bean("defaultMessageSource")
        @ConditionalOnMissingBean(name = "defaultMessageSource")
        public MessageSource defaultMessageSource(@Qualifier("valuesBundlesProperties") MessageSourceProperties valuesBundlesProperties) {
            return messageSource(new ReloadableResourceBundleMessageSource(), valuesBundlesProperties);
        }

        @Primary
        @Bean("errorMessageSource")
        @ConditionalOnMissingBean(name = "errorMessageSource")
        public MessageSource errorsMessageSource(@Qualifier("errorsBundlesProperties") MessageSourceProperties errorsBundlesProperties) {
            return messageSource(new ReloadableResourceBundleMessageSource(), errorsBundlesProperties);
        }
    }

    /**
     * 装置基于database的资源
     */
    @Configuration
    @ConditionalOnProperty(name = "base.messages.type", havingValue = "database")
    public class ReloadableDatabaseMessageSourceConfiguration {

        @Bean("defaultMessageSource")
        @ConditionalOnMissingBean(name = "defaultMessageSource")
        public MessageSource defaultMessageSource(
                @Autowired MessagesMapper messagesMapper,
                @Qualifier("valuesTablesProperties") MessageSourceProperties valuesTablesProperties
        ) {
            return messageSource(new ReloadableDatabaseMessageSource(new DefaultDatabaseMessageLoader(messagesMapper)), valuesTablesProperties);
        }

        @Bean("errorMessageSource")
        @ConditionalOnMissingBean(name = "errorMessageSource")
        public MessageSource errorMessageSource(
                @Autowired MessagesMapper messagesMapper,
                @Qualifier("errorsTablesProperties") MessageSourceProperties errorsTablesProperties
        ) {
            return messageSource(new ReloadableDatabaseMessageSource(new DefaultDatabaseMessageLoader(messagesMapper)), errorsTablesProperties);
        }

    }

}
