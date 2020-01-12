package com.weiquding.safeKeyboard.controller;

import com.weiquding.safeKeyboard.common.cache.GuavaCache;
import com.weiquding.safeKeyboard.common.cache.KeyInstance;
import com.weiquding.safeKeyboard.common.exception.CipherRuntimeException;
import com.weiquding.safeKeyboard.common.util.*;
import com.weiquding.safeKeyboard.mock.UserMock;
import com.weiquding.safeKeyboard.service.CheckUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟生成密钥与密文
 *
 * @author believeyourself
 */
@Slf4j
@RestController
public class ServerController {

    @Autowired
    private CheckUserService checkUserService;

    @Autowired
    private UserMock userMock;

    @RequestMapping(value = "/generateRNS", method = RequestMethod.POST)
    public Map<String, String> generateRNC(@RequestParam("RNC") String cipherText, String sessionId) {
        byte[] RNCAndPMS = RSAUtil.decryptByRSAPrivateKey(KeyInstance.RSA_PRIVATE_KEY, Base64.getDecoder().decode(cipherText));
        byte[] RNC = new byte[32];
        byte[] PMS = new byte[48];
        System.arraycopy(RNCAndPMS, 0, RNC, 0, RNC.length);
        System.arraycopy(RNCAndPMS, RNC.length, PMS, 0, PMS.length);
        Map<String, String> map = RandomUtil.generateRNSAndSign();
        Map<String, String> session = new HashMap<>();
        session.put("RNC", Base64.getEncoder().encodeToString(RNC));
        session.put("PMS", Base64.getEncoder().encodeToString(PMS));
        session.put("RNS", map.get("RNS"));
        GuavaCache.SERVER_CACHE.put(sessionId, session);
        return map;
    }

    @RequestMapping(value = "/submitEncryptedPassword", method = RequestMethod.POST)
    public Map<String, Object> submitEncryptedPassword(@RequestParam("password") String password, String sessionId) {
        password = URLDecoder.decode(password, StandardCharsets.UTF_8);
        // 密钥生成
        Map<String, String> session = GuavaCache.SERVER_CACHE.getIfPresent(sessionId);
        String PMS = session.get("PMS");
        String RNC = session.get("RNC");
        String RNS = session.get("RNS");
        byte[][] keyBlock = PRFUtil.generateKeyBlock(PMS, RNC, RNS);
        // 切分iv[12] + cipherText[?] + macSign[32]
        byte[] base64DecodeBytes = Base64.getDecoder().decode(password);
        byte[] iv = new byte[12];
        byte[] macDigest = new byte[32];
        byte[] cipherText = new byte[base64DecodeBytes.length - iv.length - macDigest.length];
        System.arraycopy(base64DecodeBytes, 0, iv, 0, iv.length);
        System.arraycopy(base64DecodeBytes, iv.length, cipherText, 0, cipherText.length);
        System.arraycopy(base64DecodeBytes, iv.length + cipherText.length, macDigest, 0, macDigest.length);
        log.debug("clientMacKey:{}", Arrays.toString(keyBlock[0]));
        log.debug("clientWriteKey:{}", Arrays.toString(keyBlock[2]));
        log.debug("ivParameter:{}", Arrays.toString(iv));
        log.debug("encryptedPwd:{}", Arrays.toString(cipherText));
        log.debug("macDigest:{}", Arrays.toString(macDigest));
        // 对称解密
        byte[] encryptedPwd = AESUtil.AES_256_GCM_NoPadding.decryptByAESKey(keyBlock[2], iv, cipherText);
        // 验证摘要
        byte[] serverMacDigest = HmacUtil.getMacInstance(HmacUtil.HMAC_SHA_256, keyBlock[0]).doFinal(encryptedPwd);
        if (!Arrays.equals(macDigest, serverMacDigest)) {
            throw new CipherRuntimeException("Password digest authentication failed");
        }
        // 取出真实的密码
        byte[] pwdBytes = new byte[encryptedPwd.length - 32];
        System.arraycopy(encryptedPwd, 32, pwdBytes, 0, pwdBytes.length);
        // 真实的密码
        String pwd = new String(pwdBytes, StandardCharsets.UTF_8);
        // 测试分支: 设置密码时
        //checkUserService.checkPasswordRule(pwd);
        // 对真实的密码进行哈希
        String hashedPassword = PBKDF2Util.hashingPassword(pwd);
        pwd = null;
        boolean result = checkUserService.checkUserPassword(userMock.getUserIdBySessionId(sessionId), hashedPassword);

       // 结果
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("result", result);
        return retMap;
    }
}
