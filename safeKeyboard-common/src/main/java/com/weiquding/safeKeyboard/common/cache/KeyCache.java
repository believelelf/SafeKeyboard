package com.weiquding.safeKeyboard.common.cache;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 根据客户端标识查找与其对应的公私钥
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/12
 */
public interface KeyCache {

    /**
     * 根据客户端标识查找与其对应的私钥
     *
     * @param appId 客户端标识
     * @return 私钥
     */
    PrivateKey getPrivateKeyByAppId(String appId);

    /**
     * 根据客户端标识查找与其对应的公钥
     *
     * @param appId 客户端标识
     * @return 公钥
     */
    PublicKey getPublicKeyByAppId(String appId);

}
