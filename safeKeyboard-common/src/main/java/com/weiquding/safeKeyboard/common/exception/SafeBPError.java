package com.weiquding.safeKeyboard.common.exception;

/**
 * 基础模块错误码定义
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/16
 */
public enum SafeBPError {

    /**
     * 错误信息
     */
    UNKNOWN("SAFEBP0001", "Unknown system error, please contact system administrator"),
    KEYSTORE_INSTANCE("SAFEBP0002", "An error occurred while obtaining an instance of keystore"),
    RSA_PRIVATEKEY_INSTANCE("SAFEBP0003", "An error occurred while obtaining an instance of RSAPrivateKey"),
    CERTIFICATE_INSTANCE("SAFEBP0004", "An error occurred while obtaining an instance of Certificate"),
    CERTIFICATE_LOADING("SAFEBP0005", "An error occurred while loading Certificate"),
    ORGANIZE_PARAMETERS("SAFEBP0006", "An error occurred while organizing parameters"),
    SPLICING_PARAMETERS("SAFEBP0007", "An error occurred while splicing parameters"),
    GENERATING_AES_KEY("SAFEBP0008", "An error occurred while generating the AES key"),
    GETTING_CIPHER("SAFEBP0009", "An error occurred while getting Cipher"),
    AES_DECRYPTION("SAFEBP0010", "An error occurred while using the AES key for decryption"),
    AES_ENCRYPTION("SAFEBP0011", "An error occurred while using the AES key for encryption"),
    PROCESSING_JSON_DATA("SAFEBP0012", "An error occurred while processing json data"),
    READING_RSAPRIVATEKEY("SAFEBP0013", "An error occurred reading the RSAPrivateKey"),
    READING_RSAPUBLICKEY("SAFEBP0014", "An error occurred reading the RSAPublicKey"),
    SIGNING("SAFEBP0015", "An error occurred while signing with RSAPrivateKey"),
    VERIFY_SIGN("SAFEBP0016", "An error occurred while using RSAPublicKey to verify the signature"),
    RSA_ENCRYPTION("SAFEBP0017", "An error occurred while using RSAPublicKey for encryption"),
    RSA_DECRYPTION("SAFEBP0018", "An error occurred while decrypting with RSAPrivateKey"),
    MAC_INSTANCE("SAFEBP0019", "An error occurred while getting a MAC instance"),
    ALLOW_URI("SAFEBP0020", "The encryption and decryption process does not match"),
    ENCRYPTION_KEY("SAFEBP0021", "The encryption key does not exist"),
    NO_SUCH_ALGORITHM("SAFEBP0022", "No such algorithm"),
    HASHING_PASSWORD("SAFEBP0023", "An error occurred while hashing the password"),
    PASSWORD_LOCKED("SAFEBP0024", "Your account has been locked, please try again tomorrow."),
    PASSWORD_INCORRECT("SAFEBP0025", "The password you entered is incorrect, %d chances are left"),
    INCORRECT_PASSWORD_LENGTH("SAFEBP0026", "The password you entered is incorrect, %d chances are left"),
    ILLEGAL_CHARACTERS("SAFEBP0027", "The password contains illegal characters"),
    INCREMENTING_SEQUENCES("SAFEBP0028", "The password consists entirely of equal characters or incrementing sequences"),
    DIGEST("SAFEBP0029", "Password digest authentication failed"),
    SIGNATURE_CORRUPTED("SAFEBP0030", "Signature corrupted"),
    ;

    private ErrorInfo errorInfo;

    private SafeBPError(String code, String defaultMsg) {
        this.errorInfo = ErrorInfo.item(code, defaultMsg);
    }

    public ErrorInfo getInfo() {
        return this.errorInfo;
    }

}
