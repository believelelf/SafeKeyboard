package com.weiquding.safeKeyboard.controller;

import com.google.common.reflect.TypeToken;
import com.weiquding.safeKeyboard.common.annotation.DecryptSafeFields;
import com.weiquding.safeKeyboard.common.annotation.EncryptSafeFields;
import com.weiquding.safeKeyboard.common.cache.GuavaCache;
import com.weiquding.safeKeyboard.common.cache.KeyCache;
import com.weiquding.safeKeyboard.common.cache.KeyInstance;
import com.weiquding.safeKeyboard.common.dto.*;
import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import com.weiquding.safeKeyboard.common.format.HttpTransport;
import com.weiquding.safeKeyboard.common.format.ParameterizedTypeReferenceBuilder;
import com.weiquding.safeKeyboard.common.format.Result;
import com.weiquding.safeKeyboard.common.format.ServiceType;
import com.weiquding.safeKeyboard.common.util.*;
import com.weiquding.safeKeyboard.dto.ConfirmRsp;
import com.weiquding.safeKeyboard.dto.GetEncryptedPasswordRsp;
import com.weiquding.safeKeyboard.dto.SubmitReq;
import com.weiquding.safeKeyboard.dto.SubmitRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.validation.constraints.NotBlank;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * 模拟获取密钥与密文
 *
 * @author believeyourself
 */
@Slf4j
@RestController
public class ClientController {

    @Autowired
    @Qualifier("skbsHttpTransport")
    private HttpTransport httpTransport;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private KeyCache keyCache;

    @Value("${application.appId:test_app_id}")
    private String appId;

    /**
     * 生成随机数
     */
    @RequestMapping("/generateRNC")
    public Result<RandomUtil.RNCAndPMS> generateRNC(
            @RequestHeader @NotBlank String sessionId
    ) {
        RandomUtil.RNCAndPMS rncAndPMS = RandomUtil.generateRNCAndPMS();
        Result<GenerateRnsRsp> result = httpTransport.postForObject(
                ServiceType.SKBS0001,
                "/skbs/generateRNS",
                new GenerateRnsReq(rncAndPMS.getCipherText()),
                GenerateRnsRsp.class,
                "sessionId", sessionId
        );
        String rns = result.getData().getRns();
        String sign = result.getData().getSign();
        boolean verify = RSAUtil.verifySignByRSAPublicKey(KeyInstance.RSA_PUBLIC_KEY, rns.getBytes(), MyBase64.getDecoder().decode(sign));
        if (!verify) {
            throw BaseBPError.SIGNATURE_CORRUPTED.getInfo().initialize();
        }
        rncAndPMS.setRns(rns);
        GuavaCache.CLIENT_CACHE.put(sessionId, rncAndPMS);
        // 测试使用
        return Result.success(rncAndPMS);
    }

    /**
     * 获取密码密文
     *
     * @param password  密码明文
     * @param sessionId 会话ID
     * @return 密码密文
     */
    @RequestMapping("/getEncryptedPassword")
    public Result<GetEncryptedPasswordRsp> getEncryptedPassword(
            @RequestParam("password") String password,
            @RequestHeader @NotBlank String sessionId) {
        RandomUtil.RNCAndPMS rncAndPMS = (RandomUtil.RNCAndPMS) GuavaCache.CLIENT_CACHE.getIfPresent(sessionId);
        byte[][] keyBlock = PRFUtil.generateKeyBlock(rncAndPMS.getPms(), rncAndPMS.getRnc(), rncAndPMS.getRns());
        byte[] pwdBytes = password.getBytes(StandardCharsets.UTF_8);
        // 进行摘要
        Mac macInstance = HmacUtil.getMacInstance(HmacUtil.HMAC_SHA_256, keyBlock[0]);
        byte[] macDigest = macInstance.doFinal(pwdBytes);

        // 修改点2： 向量IV取keyBlock取第keyBlock[4],不再随机生成。
        byte[] ivParameter = keyBlock[4];

        // 对称加密
        byte[] encryptedPwd = AESUtil.AES_256_CBC_PKCS7Padding.encryptByAESKey(keyBlock[2], ivParameter, pwdBytes);

        log.debug("clientMacKey:{}", Arrays.toString(keyBlock[0]));
        log.debug("clientWriteKey:{}", Arrays.toString(keyBlock[2]));
        log.debug("ivParameter.length:{}", ivParameter.length);
        log.debug("ivParameter:{}", Arrays.toString(ivParameter));
        log.debug("pwdBytes.length:{}", pwdBytes.length);
        log.debug("pwdBytes:{}", Arrays.toString(pwdBytes));
        log.debug("macDigest.length:{}", macDigest.length);
        log.debug("macDigest:{}", Arrays.toString(macDigest));
        log.debug("encryptedPwd.length:{}", encryptedPwd.length);
        log.debug("encryptedPwd:{}", Arrays.toString(encryptedPwd));

        //修改点3： IV不再拼接到密码字节中
        byte[] cipherText = new byte[encryptedPwd.length + macDigest.length];
        System.arraycopy(encryptedPwd, 0, cipherText, 0, encryptedPwd.length);
        System.arraycopy(macDigest, 0, cipherText, encryptedPwd.length, macDigest.length);

        String encryptedPwdString = MyBase64.getEncoder().encodeToString(cipherText);
        return Result.success(new GetEncryptedPasswordRsp(URLEncoder.encode(encryptedPwdString, StandardCharsets.UTF_8)));
    }

    /**
     * 模拟提交密文到服务端
     *
     * @param password  密文
     * @param sessionId 会话ID
     * @return 规则是否ok
     */
    @RequestMapping("/submitEncryptedPassword")
    public Result<SubmitEncryptedPasswordRsp> submitEncryptedPassword(
            @RequestParam("password") String password,
            @RequestHeader @NotBlank String sessionId) {
        return httpTransport.postForObject(
                ServiceType.SKBS0001,
                "/skbs/submitEncryptedPassword",
                new SubmitEncryptedPasswordReq(URLEncoder.encode(password, StandardCharsets.UTF_8)),
                SubmitEncryptedPasswordRsp.class,
                "sessionId", sessionId
        );
    }

    /**
     * 测试报文加密
     *
     * @param sessionId 会话Id
     */
    @EncryptSafeFields(fields = {"account", "toAccount", "tranAmount"}, clean = true)
    @RequestMapping("/confirm")
    public Result<ConfirmRsp> confirm(
            @RequestHeader String sessionId
    ) {
        return Result.success(new ConfirmRsp("6666", "3333", "520"));
    }

    /**
     * 测试报文解密
     *
     * @param sessionId 会话ID
     * @param submitReq 请求参数
     */
    @DecryptSafeFields(allowUris = "/confirm")
    @RequestMapping("/submit")
    public Result<SubmitRsp> submit(
            @RequestHeader String sessionId,
            @RequestBody SubmitReq submitReq
    ) {
        log.debug("submit execute...,args:[{}]", submitReq);
        return Result.success(new SubmitRsp(submitReq));
    }

    /**
     * 测试报文加密传输
     *
     * @return 返回数据
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/secureMessage")
    public Result<Map<String, Object>> secureMessage() {

        PrivateKey privateKey = keyCache.getPrivateKeyByAppId(appId);
        PublicKey publicKey = keyCache.getPublicKeyByAppId(appId);

        Map<String, Object> params = new HashMap<>();
        params.put(Constants.APPID_KEY, appId);
        params.put("className", this.getClass().getName());
        log.debug("加密前数据：[{}]", params);

        EncryptAndSignatureDto encryptedData = SecureUtil.encryptAndSignature(privateKey, publicKey, appId, params);
        log.debug("加密后数据：[{}]", encryptedData);

        Result<EncryptAndSignatureDto> retVal = httpTransport.postForObject(
                ServiceType.SKBS0001,
                "/skbs/secureMessage",
                encryptedData,
                EncryptAndSignatureDto.class);

        log.debug("解密前数据：[{}]", retVal);

        Map<String, Object> decryptedData = SecureUtil.decryptAndVerifySign(privateKey, publicKey, retVal.getData(), Map.class);
        log.debug("解密后数据：[{}]", decryptedData);

        return Result.success(decryptedData);
    }

    /**
     * 测试报文摘要
     *
     * @return 返回数据
     */
    @RequestMapping("/digestMessage")
    public Result<Map<String, Object>> digestMessage() {

        List<String> emailList = new ArrayList<>(2);
        emailList.add("believeyourself@outlook.com");
        emailList.add("xxx@qq.com");

        Map<String, Object> params = AppSecretUtil.getParams(
                keyCache.getAppSecret(appId),
                new Object[]{
                        Constants.APPID_KEY, appId,
                        "name", "believeyourself",
                        "email", emailList,
                        "address", new String[]{"xi'an", "shanghai"}
                }
        );

        Result<Map<String, Object>> retVal = httpTransport.postForObject(
                ServiceType.SKBS0001,
                "/skbs/digestMessage",
                httpTransport.map2MultiValueMap(params),
                ParameterizedTypeReferenceBuilder.fromTypeToken(
                        ParameterizedTypeReferenceBuilder.mapToken(
                                TypeToken.of(String.class),
                                TypeToken.of(Object.class)
                        )
                ),
                HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE
        );
        log.debug("服务端返回数据==>{}", retVal);
        return retVal;
    }

}
