package com.weiquding.safeKeyboard.common.aspect;

import com.weiquding.safeKeyboard.common.cache.KeyCache;
import com.weiquding.safeKeyboard.common.dto.EncryptAndSignatureDto;
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
    @Before(value =
            "@annotation(com.weiquding.safeKeyboard.common.annotation.DecryptAndVerifySign) "
                    + "&& execution(public * *.*(..,com.weiquding.safeKeyboard.common.dto.EncryptAndSignatureDto))"
                    + "&& args(.., req)"
    )
    public void decryptAndVerifySign(EncryptAndSignatureDto req) {
        log.debug("解密前参数：[{}]", req);
        String appId = req.getAppId();
        PrivateKey privateKey = keyCache.getPrivateKeyByAppId(appId);
        PublicKey publicKey = keyCache.getPublicKeyByAppId(appId);
        Map<String, Object> result = SecureUtil.decryptAndVerifySign(privateKey, publicKey, req, Map.class);
        log.debug("解密完成参数：[{}]", result);
        req.setPlainData(result);
    }

}
