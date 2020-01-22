package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5消息摘要算法
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/14
 */
public class MD5Util {

    private static final String ALGORITHM = "MD5";

    private MD5Util() {
    }

    public static byte[] md5(byte[] data) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(ALGORITHM);
            return messageDigest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw BaseBPError.NO_SUCH_ALGORITHM.getInfo().initialize(e);
        }
    }
}
