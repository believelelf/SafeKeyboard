package com.weiquding.safeKeyboard.common.provider;

import com.weiquding.safeKeyboard.common.annotation.DecryptSafeFields;
import com.weiquding.safeKeyboard.common.annotation.EncryptSafeFields;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 加解密的安全提供者
 *
 * @author wuby
 * @version V1.0
 * @date 2020/1/3
 */
public interface SafeProvider {


    /***
     * 加密输出字段
     * @param model 数据模型
     * @param metadata 注解元数据
     * @param requestUri requestURI
     * @param sessionId sessionId
     * @return 加密后实符串
     */
    String encryptSafeFields(Map<String, Object> model, EncryptSafeFields metadata, String requestUri, String sessionId);

    /***
     * 解密输入安全域字段
     * @param model 数据模型
     * @param metadata 注解元数据
     * @param sessionId sessionId
     * @return 解密后参数
     */
    Map<String, Object> decryptSafeFields(Map<String, Object> model, DecryptSafeFields metadata, String sessionId);
}
