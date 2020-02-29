package com.weiquding.safeKeyboard.common.config;

import com.weiquding.safeKeyboard.common.interceptor.LocalCacheInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC组件
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/29
 */
@Configuration
public class WebMvcConfiguration {

    /**
     * 进行Interceptor
     *
     * @return
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new LocalCacheInterceptor());
            }
        };
    }
}
