package com.weiquding.safeKeyboard.cache;

import com.weiquding.safeKeyboard.common.cache.KeyCache;
import com.weiquding.safeKeyboard.common.domain.CipherPathProperties;
import com.weiquding.safeKeyboard.common.util.PemOrDerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 客户端缓存RSA公钥与私钥
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/12
 */
@Component
public class DefaultKeyCache implements KeyCache {

    public static PrivateKey CLIENT_RSA_PRIVATE_KEY = null;

    public static PublicKey SERVER_RSA_PUBLIC_KEY = null;

    @Autowired
    private CipherPathProperties properties;

    @PostConstruct
    public void init() {
        CLIENT_RSA_PRIVATE_KEY = PemOrDerUtil.readUnencryptedPKCS1PEMRSAPrivateKeyByPEMParser(properties.getClientRsaPrivateKeyPkcs1Path());
        SERVER_RSA_PUBLIC_KEY = PemOrDerUtil.readUnencryptedPKCS8DERRSAPublicKey(properties.getServerRsaPublicKeyPkcs8Path());

    }

    @Override
    public PrivateKey getPrivateKeyByAppId(String appId) {
        return CLIENT_RSA_PRIVATE_KEY;
    }

    @Override
    public PublicKey getPublicKeyByAppId(String appId) {
        return SERVER_RSA_PUBLIC_KEY;
    }
}
