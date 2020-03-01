package com.weiquding.safeKeyboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * 服务户端
 *
 * @author believeyourself
 */
@Slf4j
@SpringBootApplication
public class SafeKeyboardServerApplication {


    @Autowired
    Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(SafeKeyboardServerApplication.class, args);
    }


    @PostConstruct
    public void init() {
        log.info("environment==>{}", environment);
    }

}