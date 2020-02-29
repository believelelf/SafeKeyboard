package com.weiquding.safeKeyboard.common.aspect;

import com.weiquding.safeKeyboard.common.cache.KeyCache;
import com.weiquding.safeKeyboard.common.dto.EncryptAndSignatureDto;
import com.weiquding.safeKeyboard.common.format.Result;
import com.weiquding.safeKeyboard.common.util.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.security.PrivateKey;
import java.security.PublicKey;

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
    @AfterReturning(pointcut =
            "@annotation(com.weiquding.safeKeyboard.common.annotation.EncryptAndSignature)"
                    + "&& execution(public com.weiquding.safeKeyboard.common.format.Result<com.weiquding.safeKeyboard.common.dto.EncryptAndSignatureDto> com.weiquding.safeKeyboard..*.*(..))",
            returning = "resultValue")
    public void encryptAndSignature(Result<EncryptAndSignatureDto> resultValue) {
        log.info("加密前参数：[{}]", resultValue);
        EncryptAndSignatureDto data = resultValue.getData();
        Assert.notNull(data, "Data must not be null");
        Assert.notNull(data.getAppId(), "AppId must not be null");
        Assert.notNull(data.getPlainData(), "PlainData must not be null");
        String appId = data.getAppId();
        PrivateKey privateKey = keyCache.getPrivateKeyByAppId(appId);
        PublicKey publicKey = keyCache.getPublicKeyByAppId(appId);
        EncryptAndSignatureDto encryptedData = SecureUtil.encryptAndSignature(privateKey, publicKey, appId, data.getPlainData());
        log.info("加密后参数：[{}]", encryptedData);
        resultValue.setData(encryptedData);
    }

}
