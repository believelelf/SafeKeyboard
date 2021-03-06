package com.weiquding.safeKeyboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 客户端
 *
 * @author believeyourself
 */
@SpringBootApplication
public class SafeKeyboardClientApplication {

    public static void main(String[] args) {
        System.setProperty("java.net.useSystemProxies", "true");
        SpringApplication.run(SafeKeyboardClientApplication.class, args);
    }

}
