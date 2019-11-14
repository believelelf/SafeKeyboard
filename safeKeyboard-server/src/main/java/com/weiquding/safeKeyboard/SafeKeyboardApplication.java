package com.weiquding.safeKeyboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 *
 * 服务户端
 * @author believeyourself
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SafeKeyboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeKeyboardApplication.class);
    }

}