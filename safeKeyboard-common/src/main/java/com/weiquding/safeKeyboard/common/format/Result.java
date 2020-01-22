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

    public static <T> Result fail(String code, String errorMsg, String cause) {
        Result<T> result = new Result<>();
        result.setHead(new ErrorDetail(code, errorMsg, cause, ErrorDetail.Severity.EXCEPTION));
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
