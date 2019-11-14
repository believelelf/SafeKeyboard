package com.weiquding.safeKeyboard.common.config;

import com.weiquding.safeKeyboard.common.cache.KeyInstance;
import com.weiquding.safeKeyboard.common.domain.CipherPathProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 * @author believeyourself
 */
@Configuration
public class KeystoreAutoConfig {

    @Bean
    public static CipherPathProperties cipherPathProperties(){
        return new CipherPathProperties();
    }


    @Bean
    public KeyInstance keyInstance(CipherPathProperties cipherPathProperties){
        return new KeyInstance(cipherPathProperties);
    }


}
