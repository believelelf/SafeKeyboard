package com.weiquding.safeKeyboard.cache;

import com.weiquding.safeKeyboard.common.cache.KeyCache;
import com.weiquding.safeKeyboard.common.domain.CipherPathProperties;
import com.weiquding.safeKeyboard.common.util.PemOrDerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FIXME believeyourself@outlook.com 修改为根据appid查找，实现配置需求
 * 从JVM中查找客户端对应的公私钥对。仅测试用。
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/12
 */
@Component
public class JvmKeyCache implements KeyCache {

    public static final Map<String, Map<String, Object>> KEY_CACHE = new ConcurrentHashMap<>();

    public static final String TEST_APP_ID = "test_app_id";

    public static final String PRIVATE_KEY = "private_key";

    public static final String PUBLIC_KEY = "public_key";

    public static final String APP_SECRET = "app_secret";

    public static final Map<String, Object> EMPTY_KEY_MAP = Collections.emptyMap();


    @Autowired
    private CipherPathProperties properties;

    @Override
    public PrivateKey getPrivateKeyByAppId(String appId) {
        return (PrivateKey) KEY_CACHE.getOrDefault(appId, EMPTY_KEY_MAP).get(PRIVATE_KEY);
    }

    @Override
    public PublicKey getPublicKeyByAppId(String appId) {
        return (PublicKey) KEY_CACHE.getOrDefault(appId, EMPTY_KEY_MAP).get(PUBLIC_KEY);
    }

    @Override
    public String getAppSecret(String appId) {
        return (String) KEY_CACHE.getOrDefault(appId, EMPTY_KEY_MAP).get(APP_SECRET);
    }

    @PostConstruct
    public void init() {
        // 测试：固定一个AppId
        PrivateKey privateKey = PemOrDerUtil.readUnencryptedPKCS1PEMRSAPrivateKeyByPEMParser(properties.getServerRsaPrivateKeyPkcs1Path());
        PublicKey publicKey = PemOrDerUtil.readUnencryptedPKCS8DERRSAPublicKey(properties.getClientRsaPublicKeyPkcs8Path());
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(PRIVATE_KEY, privateKey);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(APP_SECRET, properties.getAppSecret());
        KEY_CACHE.put(TEST_APP_ID, keyMap);
    }
}
