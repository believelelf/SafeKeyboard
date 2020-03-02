package com.weiquding.safeKeyboard.common.boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

/**
 * 应用启动后执行
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/2
 * @see SpringApplication#callRunners(org.springframework.context.ApplicationContext, org.springframework.boot.ApplicationArguments)
 */
@Slf4j
@Component
public class BootstrapApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.setProperty("http.proxyHost", "webcache.example.com");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "webcache.example.com");
        System.setProperty("https.proxyPort", "443");
        log.info(">>>>>应用已启动>>>>>");
    }
}
