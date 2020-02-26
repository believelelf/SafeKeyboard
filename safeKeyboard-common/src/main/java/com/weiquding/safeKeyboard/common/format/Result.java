package com.weiquding.safeKeyboard.common.format;

import com.weiquding.safeKeyboard.common.exception.ErrorDetail;
import lombok.Data;

/**
 * 返回报文
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/16
 */
@Data
public final class Result<T> {

    private boolean success = true;
    private ErrorDetail head;
    private T data;

    public static <T> Result success(T data) {
        Result<T> result = new Result<>();
        result.setHead(new ErrorDetail());
        result.setData(data);
        return result;
    }

    public static Result warning(String code, String errorMsg, String cause) {
        return fail(code, errorMsg, cause, ErrorDetail.Severity.WARNING);
    }

    public static Result exception(String code, String errorMsg, String cause) {
        return fail(code, errorMsg, cause, ErrorDetail.Severity.EXCEPTION);
    }

    public static Result error(String code, String errorMsg, String cause) {
        return fail(code, errorMsg, cause, ErrorDetail.Severity.ERROR);
    }

    private static <T> Result fail(String code, String errorMsg, String cause, ErrorDetail.Severity severity) {
        Result<T> result = new Result<>();
        result.setHead(new ErrorDetail(code, errorMsg, cause, severity));
        result.setSuccess(false);
        return result;
    }

    public static <T> Result fail(ErrorDetail errorDetail) {
        Result<T> result = new Result<>();
        result.setHead(errorDetail);
        result.setSuccess(false);
        return result;
    }

}
