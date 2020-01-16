package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.SafeBPError;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

/**
 * AES工具
 * <p>
 * 关于IV位数的讨论
 * https://security.stackexchange.com/questions/90848/encrypting-using-aes-256-can-i-use-256-bits-iv
 * <p>
 * The IV handling in Java is dependent on the cryptographic provider that is used.
 * The SUN provider that comes with Oracle runtimes is rather strict;
 * it requires the IV to be the same size as the block size for most modes of operation.
 * This is true even for CTR mode where you might have expected that supplying a nonce - the first bytes of the IV - should be sufficient.
 * ECB mode of course does not require an IV, so it will throw an exception if you try and supply one.
 * <p>
 * The block size of AES is always 128 bits, so a 256 bit IV is not possible for most modes of operation.
 * As already noted in a few answers, Rijndael can be configured with a block size of 256 bit, but Rijndael is not included in the standard runtime.
 * You would need an additional provider or library such as Bouncy Castle to use Rijndael.
 * The block cipher AES doesn't use an IV as input, which is also why supplying an IV for ECB mode fails.
 * Some other languages/runtimes simply ignore the IV for ECB
 * <p>
 * Now there is one cipher that does allow you to specify a 256 bit IV (or actually: nonce) and that is GCM.
 * GCM works best with a nonce of 12 bytes though. GCM converts data - includes the nonce - to a 128 bit counter for CTR mode internally.
 * <p>
 * Note that increasing the IV size does not auto-magically make the algorithm more secure.
 * If you have 256 bit input for an IV then you could use SHA-256 bit on the input and take the 128 leftmost bits instead.
 *
 * @author believeyourself
 */
@Slf4j
public class AESUtil {

    private static final String ALGORITHM = "AES";


    /**
     * 编码Key数据
     *
     * @param key 密钥
     * @return UrlSafe编码的密钥
     */
    public static String serializeString(Key key) {
        return serializeString(key.getEncoded());
    }

    /**
     * 编码Key数据
     *
     * @param encoded 密钥数据
     * @return UrlSafe编码的密钥
     */
    public static String serializeString(byte[] encoded) {
        return Base64.getUrlEncoder().encodeToString(encoded);
    }

    /**
     * 解码UrlSafe编码密钥，形成Key
     *
     * @param urlSafeBase64Key UrlSafe编码密钥
     * @return 密钥
     */
    public static Key deserializeBase64KeyToKey(String urlSafeBase64Key) {
        return new SecretKeySpec(deserializeBase64KeyToBytes(urlSafeBase64Key), ALGORITHM);
    }

    /**
     * 解码UrlSafe编码密钥，形成encoded
     *
     * @param urlSafeBase64Key UrlSafe编码密钥
     * @return 密钥
     */
    public static byte[] deserializeBase64KeyToBytes(String urlSafeBase64Key) {
        return Base64.getUrlDecoder().decode(urlSafeBase64Key);
    }


    /**
     * 根据参数数据恢复算法参数对象
     *
     * @param ivData 参数数据
     * @return AlgorithmParameterSpec
     */
    public static AlgorithmParameterSpec ivParameter(byte[] ivData) {
        return new IvParameterSpec(ivData);
    }

    /**
     * 获取向量
     *
     * @return 长度为length字节的向量
     */
    public static byte[] ivParameter(int length) {
        return RandomUtil.generateRandomBytes(length);
    }

    /**
     * 创建算法参数对象
     *
     * @param length 字节数
     * @return AlgorithmParameterSpec
     */
    public static AlgorithmParameterSpec newIvParameter(int length) {
        return new IvParameterSpec(ivParameter(length));
    }


    /**
     * 生成指定位数的AES密钥
     *
     * @param bitLen 位数
     * @return AES密钥
     */
    public static Key generateAESKey(int bitLen) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = new SecureRandom();
            keyGenerator.init(bitLen, random);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw SafeBPError.GENERATING_AES_KEY.getInfo().initialize(e);
        }
    }


    /**
     * 生成指定位数的AES密钥
     *
     * @param bitLen 位数
     * @return AES密钥数据
     */
    public static byte[] initAESKey(int bitLen) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random = new SecureRandom();
            keyGenerator.init(bitLen, random);
            return keyGenerator.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw SafeBPError.GENERATING_AES_KEY.getInfo().initialize(e);
        }
    }


    /**
     * 从密钥数据恢复Key对象
     *
     * @param encoded 密钥数据
     * @return Key对象
     */
    public static Key restoreKey(byte[] encoded) {
        try {
            return new SecretKeySpec(encoded, ALGORITHM);
        } catch (Exception e) {
            throw SafeBPError.GENERATING_AES_KEY.getInfo().initialize(e);
        }
    }


    /**
     * 获取Cipher
     *
     * @param encrypt         是否为加密模式
     * @param cipherAlgorithm 加密/解密算法 / 工作模式 / 填充方式 示例: AES/ECB/PKCS5Padding
     * @param key             密钥
     * @param param           算法参数，向量
     * @return Cipher对象
     */
    public static Cipher getCipher(boolean encrypt, String cipherAlgorithm, Key key, AlgorithmParameterSpec param) {
        try {
            int cipherMode = encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            if (param != null) {
                cipher.init(cipherMode, key, param);
            } else {
                cipher.init(cipherMode, key);
            }
            return cipher;
        } catch (Exception e) {
            throw SafeBPError.GETTING_CIPHER.getInfo().initialize(e);
        }
    }

    /**
     * 获取Cipher
     *
     * @param encrypt          是否为加密模式
     * @param cipherAlgorithm  加密/解密算法 / 工作模式 / 填充方式 示例: AES/ECB/PKCS5Padding
     * @param urlSafeBase64Key urlSafeBase64密钥
     * @param param            算法参数，向量
     * @return Cipher对象
     */
    public static Cipher getCipher(boolean encrypt, String cipherAlgorithm, String urlSafeBase64Key, AlgorithmParameterSpec param) {
        return getCipher(encrypt, cipherAlgorithm, deserializeBase64KeyToKey(urlSafeBase64Key), param);
    }

    /**
     * 获取Cipher
     *
     * @param encrypt         是否为加密模式
     * @param cipherAlgorithm 加密/解密算法 / 工作模式 / 填充方式 示例: AES/ECB/PKCS5Padding
     * @param encoded         密钥数据
     * @param param           算法参数，向量
     * @return Cipher对象
     */
    public static Cipher getCipher(boolean encrypt, String cipherAlgorithm, byte[] encoded, AlgorithmParameterSpec param) {
        return getCipher(encrypt, cipherAlgorithm, restoreKey(encoded), param);
    }

    /**
     * AES/GCM/NoPadding 加解密类
     */
    public static final class AES_256_GCM_NoPadding {

        private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";

        private static final int AES_KEY_SIZE = 256;
        /**
         * GCM recommends a 96-bit IV
         */
        private static final int GCM_IV_LENGTH = 12;

        private static final int GCM_TAG_LENGTH = 16;


        /**
         * 生成256位密钥
         *
         * @return 256位密钥
         */
        public static byte[] initAESKey() {
            return AESUtil.initAESKey(AES_KEY_SIZE);
        }

        /**
         * 获取12字节向量
         *
         * @return 长度为12字节的向量
         */
        public static byte[] ivParameter() {
            return RandomUtil.generateRandomBytes(GCM_IV_LENGTH);
        }

        /**
         * 使用AES/GCM/NoPadding进行解密
         *
         * @param encoded 密钥数据
         * @param iv      向量
         * @param data    数据
         * @return 解密后数据
         */
        public static byte[] decryptByAESKey(byte[] encoded, byte[] iv, byte[] data) {
            try {
                return getCipher(false, CIPHER_ALGORITHM, encoded, new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_DECRYPTION.getInfo().initialize(e);
            }
        }

        /**
         * 使用AES/GCM/NoPadding进行加密
         *
         * @param encoded 密钥
         * @param iv      向量
         * @param data    加密数据
         * @return 解密后数据
         */
        public static byte[] encryptByAESKey(byte[] encoded, byte[] iv, byte[] data) {
            try {
                return getCipher(true, CIPHER_ALGORITHM, encoded, new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_ENCRYPTION.getInfo().initialize(e);
            }
        }
    }

    /**
     * AES/CBC/NoPadding 加解密类
     * AES/ECB/NoPadding and  AES/CBC/NoPadding 等无填充模式，输入数据字节长度必须等于blockSize的倍数,即为16字节（128位）的倍数。
     * [输入数据存在限制条件，请在特定条件下使用]
     * javax.crypto.IllegalBlockSizeException : Input length must be multiple of 16 when decrypting with padded cipher
     * https://stackoverflow.com/questions/17234359/javax-crypto-illegalblocksizeexception-input-length-must-be-multiple-of-16-whe
     * <p>
     * The algorithm you are using, "AES", is a shorthand for "AES/ECB/NoPadding".
     * What this means is that you are using the AES algorithm with 128-bit key size and block size, with the ECB mode of operation and no padding.
     * <p>
     * In other words: you are only able to encrypt data in blocks of 128 bits or 16 bytes.
     * That's why you are getting that IllegalBlockSizeException exception.
     * <p>
     * If you want to encrypt data in sizes that are not multiple of 16 bytes, you are either going to have to use some kind of padding, or a cipher-stream.
     * For instance, you could use CBC mode (a mode of operation that effectively transforms a block cipher into a stream cipher) by specifying "AES/CBC/NoPadding" as the algorithm,
     * or PKCS5 padding by specifying "AES/ECB/PKCS5",
     * which will automatically add some bytes at the end of your data in a very specific format to make the size of the ciphertext multiple of 16 bytes,
     * and in a way that the decryption algorithm will understand that it has to ignore some data.
     * <p>
     * In any case, I strongly suggest that you stop right now what you are doing and go study some very introductory material on cryptography.
     * For instance, check Crypto I on Coursera. You should understand very well the implications of choosing one mode or another,
     * what are their strengths and, most importantly, their weaknesses. Without this knowledge,
     * it is very easy to build systems which are very easy to break.
     */
    public static final class AES_256_CBC_NoPadding {

        private static final String CIPHER_ALGORITHM = "AES/CBC/NoPadding";

        private static final int AES_KEY_SIZE = 256;

        /**
         * CBC mode requires an IV of the same size as the block size 16 (128 bit)
         */
        private static final int CBC_IV_LENGTH = 16;

        /**
         * 默认向量：{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
         * <p>
         * What causes the error “java.security.InvalidKeyException: Parameters missing”?
         * (https://stackoverflow.com/questions/17322002/what-causes-the-error-java-security-invalidkeyexception-parameters-missing?rq=1)
         * <p>
         * If you use a block-chaining mode like CBC, you need to provide an IvParameterSpec to the Cipher as well.
         */
        private static final IvParameterSpec DEFAULT_IV_PARAMETER_SPEC = new IvParameterSpec(new byte[CBC_IV_LENGTH]);

        /**
         * 生成256位密钥
         *
         * @return 256位密钥
         */
        public static byte[] initAESKey() {
            return AESUtil.initAESKey(AES_KEY_SIZE);
        }

        /**
         * 获取12字节向量
         *
         * @return 长度为12字节的向量
         */
        public static byte[] ivParameter() {
            return RandomUtil.generateRandomBytes(CBC_IV_LENGTH);
        }

        /**
         * 使用 AES/CBC/NoPadding 进行解密
         *
         * @param encoded 密钥数据
         * @param iv      向量
         * @param data    数据
         * @return 解密后数据
         */
        public static byte[] decryptByAESKey(byte[] encoded, byte[] iv, byte[] data) {
            try {
                return getCipher(false, CIPHER_ALGORITHM, encoded, AESUtil.ivParameter(iv)).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_DECRYPTION.getInfo().initialize(e);
            }
        }

        /**
         * 使用 AES/CBC/NoPadding 进行解密
         * 向量部分使用了{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }，最好使用带随机向量入参的方法。
         *
         * @param encoded 密钥数据
         * @param data    数据
         * @return 解密后数据
         */
        public static byte[] decryptByAESKey(byte[] encoded, byte[] data) {
            try {
                return getCipher(false, CIPHER_ALGORITHM, encoded, DEFAULT_IV_PARAMETER_SPEC).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_DECRYPTION.getInfo().initialize(e);
            }
        }


        /**
         * 使用 AES/CBC/NoPadding 进行加密
         *
         * @param encoded 密钥
         * @param iv      向量
         * @param data    加密数据
         * @return 解密后数据
         */
        public static byte[] encryptByAESKey(byte[] encoded, byte[] iv, byte[] data) {
            try {
                return getCipher(true, CIPHER_ALGORITHM, encoded, AESUtil.ivParameter(iv)).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_ENCRYPTION.getInfo().initialize(e);
            }
        }

        /**
         * 使用 AES/CBC/NoPadding 进行加密
         * 向量部分使用了{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }，最好使用带随机向量入参的方法。
         *
         * @param encoded 密钥
         * @param data    加密数据
         * @return 解密后数据
         */
        public static byte[] encryptByAESKey(byte[] encoded, byte[] data) {
            try {
                return getCipher(true, CIPHER_ALGORITHM, encoded, DEFAULT_IV_PARAMETER_SPEC).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_ENCRYPTION.getInfo().initialize(e);
            }
        }

    }

    /**
     * AES/CBC/PKCS5Padding 加解密类
     */
    public static final class AES_256_CBC_PKCS5Padding {

        private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

        private static final int AES_KEY_SIZE = 256;
        /**
         * CBC mode requires an IV of the same size as the block size 16 (128 bit)
         */
        private static final int CBC_IV_LENGTH = 16;

        /**
         * 默认向量：{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
         * <p>
         * What causes the error “java.security.InvalidKeyException: Parameters missing”?
         * (https://stackoverflow.com/questions/17322002/what-causes-the-error-java-security-invalidkeyexception-parameters-missing?rq=1)
         * <p>
         * If you use a block-chaining mode like CBC, you need to provide an IvParameterSpec to the Cipher as well.
         */
        private static final IvParameterSpec DEFAULT_IV_PARAMETER_SPEC = new IvParameterSpec(new byte[CBC_IV_LENGTH]);

        /**
         * 生成256位密钥
         *
         * @return 256位密钥
         */
        public static byte[] initAESKey() {
            return AESUtil.initAESKey(AES_KEY_SIZE);
        }

        /**
         * 获取12字节向量
         *
         * @return 长度为12字节的向量
         */
        public static byte[] ivParameter() {
            return RandomUtil.generateRandomBytes(CBC_IV_LENGTH);
        }

        /**
         * 使用 AES/CBC/PKCS5Padding 进行解密
         *
         * @param encoded 密钥数据
         * @param iv      向量
         * @param data    数据
         * @return 解密后数据
         */
        public static byte[] decryptByAESKey(byte[] encoded, byte[] iv, byte[] data) {
            try {
                return getCipher(false, CIPHER_ALGORITHM, encoded, AESUtil.ivParameter(iv)).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_DECRYPTION.getInfo().initialize(e);
            }
        }

        /**
         * 使用 AES/CBC/PKCS5Padding 进行解密
         * 向量部分使用了{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }，最好使用带随机向量入参的方法。
         *
         * @param encoded 密钥数据
         * @param data    数据
         * @return 解密后数据
         */
        public static byte[] decryptByAESKey(byte[] encoded, byte[] data) {
            try {
                return getCipher(false, CIPHER_ALGORITHM, encoded, DEFAULT_IV_PARAMETER_SPEC).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_DECRYPTION.getInfo().initialize(e);
            }
        }

        /**
         * 使用 AES/CBC/PKCS5Padding 进行加密
         *
         * @param encoded 密钥
         * @param iv      向量
         * @param data    加密数据
         * @return 解密后数据
         */
        public static byte[] encryptByAESKey(byte[] encoded, byte[] iv, byte[] data) {
            try {
                return getCipher(true, CIPHER_ALGORITHM, encoded, AESUtil.ivParameter(iv)).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_ENCRYPTION.getInfo().initialize(e);
            }
        }

        /**
         * 使用 AES/CBC/PKCS5Padding 进行加密
         * 向量部分使用了{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }，最好使用带随机向量入参的方法。
         *
         * @param encoded 密钥
         * @param data    加密数据
         * @return 解密后数据
         */
        public static byte[] encryptByAESKey(byte[] encoded, byte[] data) {
            try {
                return getCipher(true, CIPHER_ALGORITHM, encoded, DEFAULT_IV_PARAMETER_SPEC).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_ENCRYPTION.getInfo().initialize(e);
            }
        }

    }

    /**
     * AES/ECB/NoPadding 加解密类
     * AES/ECB/NoPadding and  AES/CBC/NoPadding 等无填充模式，输入数据字节长度必须等于blockSize的倍数,即为16字节（128位）的倍数。
     * [输入数据存在限制条件，请在特定条件下使用]
     * ECB mode cannot use IV
     */
    public static final class AES_256_ECB_NoPadding {

        private static final String CIPHER_ALGORITHM = "AES/ECB/NoPadding";

        private static final int AES_KEY_SIZE = 256;

        /**
         * 生成256位密钥
         *
         * @return 256位密钥
         */
        public static byte[] initAESKey() {
            return AESUtil.initAESKey(AES_KEY_SIZE);
        }


        /**
         * 使用 AES/ECB/NoPadding 进行解密
         * ECB mode cannot use IV
         *
         * @param encoded 密钥数据
         * @param data    数据
         * @return 解密后数据
         */
        public static byte[] decryptByAESKey(byte[] encoded, byte[] data) {
            try {
                return getCipher(false, CIPHER_ALGORITHM, encoded, null).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_DECRYPTION.getInfo().initialize(e);
            }
        }


        /**
         * 使用 AES/ECB/NoPadding 进行加密
         * ECB mode cannot use IV
         *
         * @param encoded 密钥
         * @param data    加密数据
         * @return 解密后数据
         */
        public static byte[] encryptByAESKey(byte[] encoded, byte[] data) {
            try {
                return getCipher(true, CIPHER_ALGORITHM, encoded, null).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_ENCRYPTION.getInfo().initialize(e);
            }
        }
    }

    /**
     * AES/ECB/PKCS5Padding 加解密类
     * ECB mode cannot use IV
     */
    public static final class AES_256_ECB_PKCS5Padding {

        private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

        private static final int AES_KEY_SIZE = 256;


        /**
         * 生成256位密钥
         *
         * @return 256位密钥
         */
        public static byte[] initAESKey() {
            return AESUtil.initAESKey(AES_KEY_SIZE);
        }

        /**
         * 使用 AES/ECB/PKCS5Padding 进行解密
         *
         * @param encoded 密钥数据
         * @param data    数据
         * @return 解密后数据
         */
        public static byte[] decryptByAESKey(byte[] encoded, byte[] data) {
            try {
                return getCipher(false, CIPHER_ALGORITHM, encoded, null).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_DECRYPTION.getInfo().initialize(e);
            }
        }


        /**
         * 使用 AES/ECB/PKCS5Padding 进行加密
         *
         * @param encoded 密钥
         * @param data    加密数据
         * @return 解密后数据
         */
        public static byte[] encryptByAESKey(byte[] encoded, byte[] data) {
            try {
                return getCipher(true, CIPHER_ALGORITHM, encoded, null).doFinal(data);
            } catch (Exception e) {
                throw SafeBPError.AES_ENCRYPTION.getInfo().initialize(e);
            }
        }
    }


}
