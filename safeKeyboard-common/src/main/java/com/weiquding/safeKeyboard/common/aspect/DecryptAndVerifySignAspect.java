package com.weiquding.safeKeyboard.common.aspect;

import com.weiquding.safeKeyboard.common.cache.KeyCache;
import com.weiquding.safeKeyboard.common.util.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/13
 */
@Slf4j
@Aspect
@Component
public class DecryptAndVerifySignAspect {

    @Resource
    private KeyCache keyCache;

    @SuppressWarnings("unchecked")
    @Before(value = "@annotation(com.weiquding.safeKeyboard.common.annotation.DecryptAndVerifySign) && args(..,params)")
    public void decryptAndVerifySign(Map<String, Object> params) {
        log.info("解密前参数：[{}]", params);
        String appId = (String) params.get(SecureUtil.APPID_KEY);
        PrivateKey privateKey = keyCache.getPrivateKeyByAppId(appId);
        PublicKey publicKey = keyCache.getPublicKeyByAppId(appId);
        Map<String, Object> result = SecureUtil.decryptAndVerifySign(privateKey, publicKey, params);
        log.info("解密完成参数：[{}]", result);
        params.clear();
        params.putAll(result);
    }

}
