package com.weiquding.safeKeyboard.common.aspect;

import com.weiquding.safeKeyboard.common.annotation.EncryptSafeFields;
import com.weiquding.safeKeyboard.common.provider.SafeProvider;
import com.weiquding.safeKeyboard.common.util.Constants;
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
import org.springframework.ui.Model;

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
            + "&& execution(public java.util.Map<String,Object> com.weiquding.safeKeyboard..*.*(..))"
    )
    public void encryptSafeFieldsPointcut() {
    }

    @AfterReturning(value = "encryptSafeFieldsPointcut() && args(..,model,params)", returning = "resultValue")
    public Map<String, Object> encryptSafeFields(JoinPoint joinPoint, Object resultValue, Model model, Map<String, Object> params) {
        // Controller方法只能是Map<String,Object>，由切点表达式限定
        Map<String, Object> result = (Map<String, Object>) resultValue;

        log.info("aspect[encryptSafeFields] execute..., model:[{}],parameterMap[{}]", model, params);

        Method targetMethod = MethodSignature.class.cast(joinPoint.getSignature()).getMethod();
        EncryptSafeFields metadata = targetMethod.getAnnotation(EncryptSafeFields.class);
        SafeProvider safeProvider = this.applicationContext.getBean(metadata.safeProvider(), SafeProvider.class);

        String[] fields = metadata.fields();
        Map<String, Object> toEncryptedParams = getParams(result, fields, metadata.clean());
        String encryptedMsg = safeProvider.encryptSafeFields(
                toEncryptedParams,
                metadata,
                (String) model.getAttribute(Constants.REQUEST_URI),
                (String) params.get("sessionId")
        );
        result.put(metadata.name(), encryptedMsg);
        return result;
    }

    private Map<String, Object> getParams(Map<String, Object> params, String[] fields, boolean clean) {
        Map<String, Object> retMap = new HashMap<>();
        for (String field : fields) {
            retMap.put(field, params.get(field));
            if (clean) {
                params.remove(field);
            }
        }
        return retMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
