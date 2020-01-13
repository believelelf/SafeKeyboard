package com.weiquding.safeKeyboard.common.aspect;

import com.weiquding.safeKeyboard.common.cache.KeyCache;
import com.weiquding.safeKeyboard.common.util.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * 加密及签名数据切面
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/13
 */
@Component
@Aspect
@Slf4j
public class EncryptAndSignatureAspect {

    @Resource
    private KeyCache keyCache;

    @SuppressWarnings("unchecked")
    @AfterReturning(pointcut = "@annotation(com.weiquding.safeKeyboard.common.annotation.EncryptAndSignature)", returning = "message")
    public void encryptAndSignature(Map<String, Object> message) {
        log.info("加密前参数：[{}]", message);
        String appId = (String) message.get(SecureUtil.APPID_KEY);
        PrivateKey privateKey = keyCache.getPrivateKeyByAppId(appId);
        PublicKey publicKey = keyCache.getPublicKeyByAppId(appId);
        Map<String, Object> retVal = SecureUtil.encryptAndSignature(privateKey, publicKey, message);
        log.info("加密后参数：[{}]", retVal);
        message.clear();
        message.putAll(retVal);
    }

}
