package com.weiquding.safeKeyboard.common.aspect;

import com.weiquding.safeKeyboard.common.annotation.DecryptSafeFields;
import com.weiquding.safeKeyboard.common.provider.SafeProvider;
import com.weiquding.safeKeyboard.common.util.ReflectUtil;
import com.weiquding.safeKeyboard.common.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 解密切面
 *
 * @author wuby
 * @version V1.0
 * @date 2020/1/6
 */
@Aspect
@Component
@Slf4j
public class DecryptSafeFieldsAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Pointcut("(@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller))"
            + "&& @annotation(com.weiquding.safeKeyboard.common.annotation.DecryptSafeFields)"
            + "&& execution(public com.weiquding.safeKeyboard.common.format.Result com.weiquding.safeKeyboard..*.*(..))"
    )
    public void decryptSafeFieldsPointcut() {
    }

    @Before("decryptSafeFieldsPointcut() && args(..,req)")
    public void decryptSafeFields(JoinPoint joinPoint, Object req) {
        log.info("aspect[decryptSafeFields] execute..., req[{}]", req);
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DecryptSafeFields metadata = targetMethod.getAnnotation(DecryptSafeFields.class);
        SafeProvider safeProvider = this.applicationContext.getBean(metadata.safeProvider(), SafeProvider.class);
        Map<String, Object> retMap = safeProvider.decryptSafeFields((String) ReflectUtil.getFieldValue(req, metadata.name()), metadata, WebUtil.getRequest().getHeader("sessionId"));
        for (Map.Entry<String, Object> entry : retMap.entrySet()) {
            ReflectUtil.setSafeFieldValue(req, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
