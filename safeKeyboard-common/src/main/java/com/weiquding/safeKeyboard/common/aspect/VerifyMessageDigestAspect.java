package com.weiquding.safeKeyboard.common.aspect;

import com.weiquding.safeKeyboard.common.cache.KeyCache;
import com.weiquding.safeKeyboard.common.util.AppSecretUtil;
import com.weiquding.safeKeyboard.common.util.Constants;
import com.weiquding.safeKeyboard.common.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 验证消息摘要切面逻辑
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/1
 */
@Slf4j
@Aspect
@Component
public class VerifyMessageDigestAspect {

    @Resource
    private KeyCache keyCache;

    @SuppressWarnings("unchecked")
    @Before(value =
            "@annotation(com.weiquding.safeKeyboard.common.annotation.VerifyMessageDigest) "
                    + "&& execution(public * *.*(..))"
                    + "&& args(.., req)"
    )
    public void verifyMessageDigest(Object req) {
        log.debug("待验证摘要参数：[{}]", req);
        String appId = (String) ReflectUtil.getFieldValue(req, Constants.APPID_KEY);
        String appSecret = keyCache.getAppSecret(appId);
        AppSecretUtil.verifyMessageDigest(appSecret, req);
    }
}
