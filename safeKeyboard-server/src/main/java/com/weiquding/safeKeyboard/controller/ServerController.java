package com.weiquding.safeKeyboard.controller;

import com.weiquding.safeKeyboard.cache.JvmKeyCache;
import com.weiquding.safeKeyboard.common.annotation.DecryptAndVerifySign;
import com.weiquding.safeKeyboard.common.annotation.EncryptAndSignature;
import com.weiquding.safeKeyboard.common.annotation.VerifyMessageDigest;
import com.weiquding.safeKeyboard.common.cache.GuavaCache;
import com.weiquding.safeKeyboard.common.cache.KeyInstance;
import com.weiquding.safeKeyboard.common.dto.*;
import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import com.weiquding.safeKeyboard.common.format.Result;
import com.weiquding.safeKeyboard.common.util.*;
import com.weiquding.safeKeyboard.mock.UserMock;
import com.weiquding.safeKeyboard.service.CheckUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping(value = "/skbs",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ServerController {

    @Autowired
    private CheckUserService checkUserService;

    @Autowired
    private UserMock userMock;

    /**
     * 测试生成随机数
     *
     * @param sessionId
     * @param generateRnsReq
     * @return
     */
    @PostMapping(value = "/generateRNS")
    public Result<GenerateRnsRsp> generateRNC(
            @RequestHeader String sessionId,
            @RequestBody GenerateRnsReq generateRnsReq
    ) {
        byte[] rncAndPMSBytes = RSAUtil.decryptByRSAPrivateKey(KeyInstance.RSA_PRIVATE_KEY, MyBase64.getDecoder().decode(generateRnsReq.getRnc()));
        byte[] rnc = new byte[32];
        byte[] pms = new byte[48];
        System.arraycopy(rncAndPMSBytes, 0, rnc, 0, rnc.length);
        System.arraycopy(rncAndPMSBytes, rnc.length, pms, 0, pms.length);
        RandomUtil.RNSAndSign rnsAndSign = RandomUtil.generateRNSAndSign();
        RandomUtil.RNCAndPMS rncAndPMS = new RandomUtil.RNCAndPMS(
                MyBase64.getEncoder().encodeToString(rnc),
                MyBase64.getEncoder().encodeToString(pms),
                rnsAndSign.getRns(),
                null
        );
        GuavaCache.SERVER_CACHE.put(sessionId, rncAndPMS);
        return Result.success(new GenerateRnsRsp(rnsAndSign.getRns(), rnsAndSign.getSign()));
    }

    /***
     * 测试提交密码密文
     * @param sessionId
     * @param req
     * @return
     */
    @PostMapping(value = "/submitEncryptedPassword")
    public Result<SubmitEncryptedPasswordRsp> submitEncryptedPassword(
            @RequestHeader String sessionId,
            @RequestBody SubmitEncryptedPasswordReq req
    ) {
        String password = URLDecoder.decode(req.getPassword(), StandardCharsets.UTF_8);
        // 密钥生成
        RandomUtil.RNCAndPMS rncAndPMS = (RandomUtil.RNCAndPMS) GuavaCache.SERVER_CACHE.getIfPresent(sessionId);
        byte[][] keyBlock = PRFUtil.generateKeyBlock(rncAndPMS.getPms(), rncAndPMS.getRnc(), rncAndPMS.getRns());
        // 切分iv[12] + cipherText[?] + macSign[32]
        byte[] base64DecodeBytes = MyBase64.getDecoder().decode(password);
        byte[] iv = keyBlock[4];
        byte[] macDigest = new byte[32];
        byte[] cipherText = new byte[base64DecodeBytes.length - macDigest.length];
        System.arraycopy(base64DecodeBytes, 0, cipherText, 0, cipherText.length);
        System.arraycopy(base64DecodeBytes, cipherText.length, macDigest, 0, macDigest.length);

        log.debug("clientMacKey:{}", Arrays.toString(keyBlock[0]));
        log.debug("clientWriteKey:{}", Arrays.toString(keyBlock[2]));
        log.debug("ivParameter:{}", Arrays.toString(iv));
        log.debug("encryptedPwd:{}", Arrays.toString(cipherText));
        log.debug("macDigest:{}", Arrays.toString(macDigest));

        // 对称解密
        byte[] encryptedPwd = AESUtil.AES_256_CBC_PKCS7Padding.decryptByAESKey(keyBlock[2], iv, cipherText);
        // 验证摘要
        byte[] serverMacDigest = HmacUtil.getMacInstance(HmacUtil.HMAC_SHA_256, keyBlock[0]).doFinal(encryptedPwd);
        if (!Arrays.equals(macDigest, serverMacDigest)) {
            throw BaseBPError.DIGEST.getInfo().initialize();
        }
        // 真实的密码
        String pwd = new String(encryptedPwd, StandardCharsets.UTF_8);
        // 验证密码规则
        checkUserService.checkPasswordRule(pwd);
        // 对真实的密码进行哈希
        String hashedPassword = PBKDF2Util.hashingPassword(pwd);
        boolean result = checkUserService.checkUserPassword(userMock.getUserIdBySessionId(sessionId), hashedPassword);
        return Result.success(new SubmitEncryptedPasswordRsp(result));
    }

    /**
     * 测试报文加解密
     *
     * @param req
     * @return
     */
    @DecryptAndVerifySign
    @EncryptAndSignature
    @PostMapping("/secureMessage")
    public Result<EncryptAndSignatureDto> secureMessage(@RequestBody EncryptAndSignatureDto req) {
        Map<String, Object> data = new HashMap<>(2);
        data.put(Constants.APPID_KEY, JvmKeyCache.TEST_APP_ID);
        data.put("className", this.getClass().getName());
        return Result.success(new EncryptAndSignatureDto<Map<String, Object>>(JvmKeyCache.TEST_APP_ID, data));
    }

    /**
     * 测试报文摘要
     *
     * @return
     */
    @VerifyMessageDigest
    @PostMapping(value = "/digestMessage",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Map<String, Object>> digestMessage(@RequestParam Map<String, Object> req) {
        Map<String, Object> data = new HashMap<>(2);
        data.put(Constants.APPID_KEY, JvmKeyCache.TEST_APP_ID);
        data.put("className", this.getClass().getName());
        return Result.success(data);
    }


    /**
     * 测试文件加密上传
     *
     * @return 文件加密测试结果
     */
    @PostMapping(value = "/secureUploadFiles",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Map<String, Object>> secureUploadFiles(@RequestParam Map<String, Object> req) {

        Map<String, Object> data = new HashMap<>(2);
        data.put(Constants.APPID_KEY, JvmKeyCache.TEST_APP_ID);
        data.put("className", this.getClass().getName());
        return Result.success(data);
    }
}
