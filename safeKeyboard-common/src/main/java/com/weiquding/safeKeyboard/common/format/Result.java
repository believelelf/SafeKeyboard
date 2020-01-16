package com.weiquding.safeKeyboard.common.format;

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

    public static final String NORMAL_CODE = "SAFEBP0000";

    private boolean success = true;
    private String code = NORMAL_CODE;
    private String errorMsg;
    private T data;

    public static <T> Result success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        return result;
    }

    public static <T> Result fail(String code, String errorMsg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setErrorMsg(errorMsg);
        result.setSuccess(false);
        return result;
    }

}
