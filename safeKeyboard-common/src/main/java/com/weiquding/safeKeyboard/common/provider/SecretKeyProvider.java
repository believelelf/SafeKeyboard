package com.weiquding.safeKeyboard.common.provider;

/**
 * 密钥提供者
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/1
 */
public interface SecretKeyProvider {

    /**
     * 获取服务端密钥
     *
     * @param key         密钥key
     * @param isNewCreate 不存在是否新建
     * @return 密钥
     */
    byte[] getSecretKey(String key, boolean isNewCreate);
}
