package com.weiquding.safeKeyboard.common.config;

import com.weiquding.safeKeyboard.common.cache.KeyInstance;
import com.weiquding.safeKeyboard.common.domain.CipherPathProperties;
import com.weiquding.safeKeyboard.common.provider.GuavaCacheAESSafeProvider;
import com.weiquding.safeKeyboard.common.provider.GuavaCacheSecretKeyProvider;
import com.weiquding.safeKeyboard.common.provider.SafeProvider;
import com.weiquding.safeKeyboard.common.provider.SecretKeyProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 配置类
 *
 * @author believeyourself
 */
@Configuration
public class KeystoreConfiguration {

    @Bean
    @Primary
    public static CipherPathProperties cipherPathProperties() {
        return new CipherPathProperties();
    }


    @Bean
    public KeyInstance keyInstance(CipherPathProperties cipherPathProperties) {
        return new KeyInstance(cipherPathProperties);
    }

    @Bean("secretKeyProvider")
    @ConditionalOnMissingBean(name = "secretKeyProvider")
    public SecretKeyProvider secretKeyProvider() {
        return new GuavaCacheSecretKeyProvider();
    }

    @Bean("safeProvider")
    @ConditionalOnMissingBean(name = "safeProvider")
    public SafeProvider safeProvider(SecretKeyProvider secretKeyProvider) {
        return new GuavaCacheAESSafeProvider(secretKeyProvider);
    }

}
