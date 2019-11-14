package com.weiquding.safeKeyboard.common.exception;

/**
 * 加解密相关异常
 *
 * @author believeyourself
 */
public class CipherRuntimeException extends RuntimeException {

    /**
     * Call the superior
     */
    public CipherRuntimeException() {
        super();
    }

    /**
     * Call the superior
     *
     * @param message the detail message
     */
    public CipherRuntimeException(String message) {
        super(message);
    }

    /**
     * Call the superior
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public CipherRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Call the superior
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public CipherRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Call the superior
     *
     * @param message            the detail message.
     * @param cause              the cause.  (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression  whether or not suppression is enabled
     *                           or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     * @since 1.7
     */
    protected CipherRuntimeException(String message, Throwable cause,
                                     boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}