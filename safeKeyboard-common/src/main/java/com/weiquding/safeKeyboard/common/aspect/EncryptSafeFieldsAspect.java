package com.weiquding.safeKeyboard.common.aspect;

import com.weiquding.safeKeyboard.common.annotation.EncryptSafeFields;
import com.weiquding.safeKeyboard.common.format.Result;
import com.weiquding.safeKeyboard.common.provider.SafeProvider;
import com.weiquding.safeKeyboard.common.util.ReflectUtil;
import com.weiquding.safeKeyboard.common.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * 加密切面
 *
 * @author wuby
 * @version V1.0
 * @date 2020/1/6
 */
@Aspect
@Component
@Slf4j
public class EncryptSafeFieldsAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Pointcut("(@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller))"
            + "&& @annotation(com.weiquding.safeKeyboard.common.annotation.EncryptSafeFields) "
            + "&& execution(public com.weiquding.safeKeyboard.common.format.Result *.*(..))"
    )
    public void encryptSafeFieldsPointcut() {
    }

    @AfterReturning(value = "encryptSafeFieldsPointcut()", returning = "resultValue")
    public void encryptSafeFields(JoinPoint joinPoint, Result<?> resultValue) {
        log.info("aspect[encryptSafeFields] execute..., resultValue:[{}],", resultValue);
        if (resultValue.getData() == null) {
            throw new IllegalStateException("data must not be null");
        }
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        EncryptSafeFields metadata = targetMethod.getAnnotation(EncryptSafeFields.class);
        SafeProvider safeProvider = this.applicationContext.getBean(metadata.safeProvider(), SafeProvider.class);

        String[] fields = metadata.fields();
        Map<String, Object> toEncryptedParams = getParams(resultValue.getData(), fields, metadata.clean());
        String encryptedMsg = safeProvider.encryptSafeFields(
                toEncryptedParams,
                metadata,
                WebUtil.getRequest().getRequestURI(),
                WebUtil.getRequest().getHeader("sessionId")
        );
        ReflectUtil.setSafeFieldValue(resultValue.getData(), metadata.name(), encryptedMsg);
    }

    /**
     * 只处理Map与JavaBean
     *
     * @param data   方法返回值
     * @param fields 待加密字段
     * @param clean  是否清理
     * @return 待加密参数
     */
    private Map<String, Object> getParams(Object data, String[] fields, boolean clean) {
        Map<String, Object> retMap = new HashMap<>(fields.length);
        for (String field : fields) {
            retMap.put(field, ReflectUtil.getFieldValue(data, field));
            if (clean) {
                ReflectUtil.removeFieldValue(data, field);
            }
        }
        return retMap;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
