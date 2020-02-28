package com.weiquding.safeKeyboard.common.config;

import com.weiquding.safeKeyboard.common.format.DefaultExchangeHandler;
import com.weiquding.safeKeyboard.common.format.ExchangeHandler;
import com.weiquding.safeKeyboard.common.format.HttpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 报文传输相关配置
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/27
 */
@Configuration
public class FormatConfiguration {

    @Value("${base.http.rootUri.skbs}")
    private String skbsRootUri;

    @Bean("skbsHttpTransport")
    @ConditionalOnMissingBean(name = "skbsHttpTransport")
    public HttpTransport httpTransport(RestTemplate restTemplate, ExchangeHandler exchangeHandler) {
        return new HttpTransport(skbsRootUri, restTemplate, exchangeHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExchangeHandler exchangeHandler() {
        return new DefaultExchangeHandler();
    }

}
