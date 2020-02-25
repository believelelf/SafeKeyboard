package com.weiquding.safeKeyboard.common.exception;

/**
 * 基础模块错误码定义
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/16
 */
public enum BaseBPError {

    /**
     * 错误信息
     */
    UNKNOWN("BASEBP0001", "Unknown system error, please contact system administrator"),
    TIMEOUT("BASEBP0002", "The system is busy, please check the details and confirm the result"),
    RSA_PRIVATEKEY_INSTANCE("BASEBP0003", "An error occurred while obtaining an instance of RSAPrivateKey"),
    CERTIFICATE_INSTANCE("BASEBP0004", "An error occurred while obtaining an instance of Certificate"),
    CERTIFICATE_LOADING("BASEBP0005", "An error occurred while loading Certificate"),
    ORGANIZE_PARAMETERS("BASEBP0006", "An error occurred while organizing parameters"),
    SPLICING_PARAMETERS("BASEBP0007", "An error occurred while splicing parameters"),
    GENERATING_AES_KEY("BASEBP0008", "An error occurred while generating the AES key"),
    GETTING_CIPHER("BASEBP0009", "An error occurred while getting Cipher"),
    AES_DECRYPTION("BASEBP0010", "An error occurred while using the AES key for decryption"),
    AES_ENCRYPTION("BASEBP0011", "An error occurred while using the AES key for encryption"),
    PROCESSING_JSON_DATA("BASEBP0012", "An error occurred while processing json data"),
    READING_RSAPRIVATEKEY("BASEBP0013", "An error occurred reading the RSAPrivateKey"),
    READING_RSAPUBLICKEY("BASEBP0014", "An error occurred reading the RSAPublicKey"),
    SIGNING("BASEBP0015", "An error occurred while signing with RSAPrivateKey"),
    VERIFY_SIGN("BASEBP0016", "An error occurred while using RSAPublicKey to verify the signature"),
    RSA_ENCRYPTION("BASEBP0017", "An error occurred while using RSAPublicKey for encryption"),
    RSA_DECRYPTION("BASEBP0018", "An error occurred while decrypting with RSAPrivateKey"),
    MAC_INSTANCE("BASEBP0019", "An error occurred while getting a MAC instance"),
    ALLOW_URI("BASEBP0020", "The encryption and decryption process does not match"),
    ENCRYPTION_KEY("BASEBP0021", "The encryption key does not exist"),
    NO_SUCH_ALGORITHM("BASEBP0022", "No such algorithm"),
    HASHING_PASSWORD("BASEBP0023", "An error occurred while hashing the password"),
    PASSWORD_LOCKED("BASEBP0024", "Your account has been locked, please try again tomorrow."),
    PASSWORD_INCORRECT("BASEBP0025", "The password you entered is incorrect, %d chances are left"),
    INCORRECT_PASSWORD_LENGTH("BASEBP0026", "The password you entered is incorrect, %d chances are left"),
    ILLEGAL_CHARACTERS("BASEBP0027", "The password contains illegal characters"),
    INCREMENTING_SEQUENCES("BASEBP0028", "The password consists entirely of equal characters or incrementing sequences"),
    DIGEST("BASEBP0029", "Password digest authentication failed"),
    SIGNATURE_CORRUPTED("BASEBP0030", "Signature corrupted"),
    KEYSTORE_INSTANCE("BASEBP0031", "An error occurred while obtaining an instance of keystore"),
    GETTING_CIPHERINPUTSTREAM("BASEBP0032", "An error occurred while getting CipherInputStream"),
    GETTING_CIPHEROUTPUTSTREAM("BASEBP0033", "An error occurred while getting CipherOutputStream"),

    ;

    private ErrorInfo errorInfo;

    private BaseBPError(String code, String defaultMsg) {
        this.errorInfo = ErrorInfo.item(code, defaultMsg);
    }

    public ErrorInfo getInfo() {
        return this.errorInfo;
    }

}
