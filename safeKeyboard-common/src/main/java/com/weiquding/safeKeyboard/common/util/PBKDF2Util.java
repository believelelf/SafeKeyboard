package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.cache.KeyInstance;
import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;

/**
 * PBKDF2 算法工具类
 *
 * @author believeyourself
 */
public class PBKDF2Util {

    public static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    /**
     * 对密进行哈希操作
     *
     * @param password 明文密码
     * @return hashed password
     */
    public static String hashingPassword(String password) {
        return hashingPassword(password, KeyInstance.PBKDF2_SLAT);
    }

    /**
     * 对密进行哈希操作
     *
     * @param password 明文密码
     * @param slat     盐
     * @return hashed password
     */
    public static String hashingPassword(String password, byte[] slat) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), slat, 10000, 512);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Hex.encodeHexString(hash);
        } catch (Exception e) {
            throw BaseBPError.HASHING_PASSWORD.getInfo().initialize(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(PBKDF2Util.hashingPassword("901823", MyBase64.getDecoder().decode("Kxi1PnJd8JqcOKoX3fv/aQ==")));
    }

}
