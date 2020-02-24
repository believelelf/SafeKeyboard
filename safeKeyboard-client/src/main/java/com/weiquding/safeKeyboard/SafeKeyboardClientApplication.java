package com.weiquding.safeKeyboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 客户端
 *
 * @author believeyourself
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SafeKeyboardClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeKeyboardClientApplication.class, args);
    }

}
