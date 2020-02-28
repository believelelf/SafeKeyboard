package com.weiquding.safeKeyboard.controller;

import com.weiquding.safeKeyboard.common.annotation.DecryptSafeFields;
import com.weiquding.safeKeyboard.common.annotation.EncryptSafeFields;
import com.weiquding.safeKeyboard.common.cache.GuavaCache;
import com.weiquding.safeKeyboard.common.cache.KeyCache;
import com.weiquding.safeKeyboard.common.cache.KeyInstance;
import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import com.weiquding.safeKeyboard.common.format.Result;
import com.weiquding.safeKeyboard.common.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.crypto.Mac;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟获取密钥与密文
 *
 * @author believeyourself
 */
@Slf4j
@RestController
public class ClientController {

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private KeyCache keyCache;

    /**
     *
     */
    @RequestMapping("/generateRNC")
    public Result generateRNC(String sessionId) {
        Map<String, String> rncAndPMS = RandomUtil.generateRNCAndPMS();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("RNC", rncAndPMS.get("cipherText"));
        map.add("sessionId", sessionId);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        Map<String, String> retVal = restTemplate.postForObject("http://localhost:8082/generateRNS", request, Map.class);
        String RNS = retVal.get("RNS");
        String sign = retVal.get("sign");
        boolean verify = RSAUtil.verifySignByRSAPublicKey(KeyInstance.RSA_PUBLIC_KEY, RNS.getBytes(), Base64.getDecoder().decode(sign));
        if (!verify) {
            throw BaseBPError.SIGNATURE_CORRUPTED.getInfo().initialize();
        }
        rncAndPMS.put("RNS", RNS);
        rncAndPMS.remove("cipherText");
        GuavaCache.CLIENT_CACHE.put(sessionId, rncAndPMS);
        // 测试用
        return Result.success(rncAndPMS);
    }

    /**
     * 获取密文
     *
     * @param password
     * @return
     */
    @RequestMapping("/getEncryptedPassword")
    public Map<String, String> getEncryptedPassword(@RequestParam("password") String password, String sessionId) {
        Map<String, String> map = new HashMap<>(1);
        Map<String, String> session = GuavaCache.CLIENT_CACHE.getIfPresent(sessionId);
        String PMS = session.get("PMS");
        String RNC = session.get("RNC");
        String RNS = session.get("RNS");
        byte[][] keyBlock = PRFUtil.generateKeyBlock(PMS, RNC, RNS);
        byte[] pwdBytes = password.getBytes(StandardCharsets.UTF_8);
        // 修改点1： 不拼接randomBytes, 不做混淆了。以使前端相同key加密的两次密文一致，可用于前端密码输入一致的判断。

        // 进行摘要
        Mac macInstance = HmacUtil.getMacInstance(HmacUtil.HMAC_SHA_256, keyBlock[0]);
        byte[] macDigest = macInstance.doFinal(pwdBytes);

        // 修改点2： 向量IV取keyBlock取第keyBlock[4],不再随机生成。
        byte[] ivParameter = keyBlock[4];

        // 对称加密
        byte[] encryptedPwd = AESUtil.AES_256_CBC_PKCS7Padding.decryptByAESKey(keyBlock[2], ivParameter, pwdBytes);

        log.info("clientMacKey:{}", Arrays.toString(keyBlock[0]));
        log.info("clientWriteKey:{}", Arrays.toString(keyBlock[2]));
        log.info("ivParameter.length:{}", ivParameter.length);
        log.info("ivParameter:{}", Arrays.toString(ivParameter));
        log.info("pwdBytes.length:{}", pwdBytes.length);
        log.info("pwdBytes:{}", Arrays.toString(pwdBytes));
        log.info("macDigest.length:{}", macDigest.length);
        log.info("macDigest:{}", Arrays.toString(macDigest));
        log.info("encryptedPwd.length:{}", encryptedPwd.length);
        log.info("encryptedPwd:{}", Arrays.toString(encryptedPwd));

        //修改点3： IV不再拼接到密码字节中
        byte[] cipherText = new byte[encryptedPwd.length + macDigest.length];
        System.arraycopy(encryptedPwd, 0, cipherText, 0, encryptedPwd.length);
        System.arraycopy(macDigest, 0, cipherText, encryptedPwd.length, macDigest.length);

        String encryptedPwdString = Base64.getEncoder().encodeToString(cipherText);
        map.put("password", URLEncoder.encode(encryptedPwdString, StandardCharsets.UTF_8));
        return map;
    }

    /**
     * 提交密文
     *
     * @param password
     * @return
     */
    @RequestMapping("/submitEncryptedPassword")
    public Map<String, Object> submitEncryptedPassword(@RequestParam("password") String password, String sessionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("password", URLEncoder.encode(password, StandardCharsets.UTF_8));
        map.add("sessionId", sessionId);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        Map<String, Object> retVal = restTemplate.postForObject("http://localhost:8082/submitEncryptedPassword", request, Map.class);

        return retVal;

    }

    /**
     * 测试报文加密
     *
     * @param sessionId
     * @param model
     * @param params
     */
    @EncryptSafeFields(fields = {"account", "toAccount", "tranAmount"})
    @RequestMapping("/confirm")
    public Map<String, Object> confirm(String sessionId, Model model, @RequestParam Map<String, Object> params) {
        log.info("confirm execute..., args:[{}]", model);
        model.addAttribute("operation", "confirm");
        // 放置返回参数
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("account", params.get("account"));
        returnMap.put("toAccount", params.get("toAccount"));
        returnMap.put("tranAmount", params.get("tranAmount"));
        return returnMap;
    }

    /**
     * 测试报文解密
     *
     * @param sessionId
     * @param model
     * @param params
     */
    @DecryptSafeFields(allowUris = "/confirm")
    @RequestMapping("/submit")
    public Map<String, Object> submit(String sessionId, Model model, @RequestParam Map<String, Object> params) {
        log.info("submit execute...,args:[{}]", model);
        model.addAttribute("operation", "submit");

        // 放置返回参数
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.putAll(model.asMap());
        return returnMap;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/secureMessage")
    public Map<String, Object> secureMessage() {

        String appId = "test_app_id";
        PrivateKey privateKey = keyCache.getPrivateKeyByAppId(appId);
        PublicKey publicKey = keyCache.getPublicKeyByAppId(appId);

        Map<String, Object> params = new HashMap<>();
        params.put(SecureUtil.APPID_KEY, appId);
        params.put("className", this.getClass().getName());
        log.info("加密前数据：[{}]", params);
        Map<String, Object> retMap = SecureUtil.encryptAndSignature(privateKey, publicKey, params);
        log.info("加密后数据：[{}]", retMap);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : retMap.entrySet()) {
            map.add(entry.getKey(), entry.getValue());
        }
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        Map<String, Object> retVal = restTemplate.postForObject("http://localhost:8082/secureMessage", request, Map.class);
        log.info("解密前数据：[{}]", retVal);
        retVal = SecureUtil.decryptAndVerifySign(privateKey, publicKey, retVal);
        log.info("解密后数据：[{}]", retVal);
        return retVal;
    }

}
