package com.weiquding.safeKeyboard.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weiquding.safeKeyboard.common.util.Constants;
import com.weiquding.safeKeyboard.common.util.WebUtil;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * 错误信息封装
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/22
 */
@Data
public class ErrorDetail {

    /**
     * 错误发生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZZZZ")
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    /**
     * 错误序号
     */
    private String id;

    /**
     * 返回码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 原错误信息
     */
    private String cause;

    /**
     * 发生错误的主机
     */
    private String hostName;

    /**
     * 帮助链接
     */
    private String helpLink;

    /**
     * 请求URI
     */
    private String path;

    /**
     * Item the message refers to, if applicable. This is used to indicate a missing or incorrect value.
     * 信息所涉及的项目，如果适用的话。这用于指示缺少或不正确的值。
     */
    private String refersTo;

    /**
     * Severity of the message: Success, Warning, Error and Exception.
     */
    private Severity severity = Severity.SUCCESS;

    private void initId() {
        try {
            String traceNo = WebUtil.getRequest().getHeader(Constants.TRACE_NO);
            this.id = (traceNo != null) ? traceNo : UUID.randomUUID().toString();
        } catch (Exception e) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public ErrorDetail() {
        initId();
    }

    public ErrorDetail(String code, String message, String cause) {
        this.code = code;
        this.message = message;
        this.cause = cause;
        initId();
    }

    public ErrorDetail(String code, String message, String cause, Severity severity) {
        this.code = code;
        this.message = message;
        this.cause = cause;
        this.severity = severity;
        initId();
    }

    /**
     * Severity of the message: Success, Warning, Error and Exception.
     * 消息的严重程度
     */
    public enum Severity {
        /***
         * 成功
         */
        SUCCESS,
        /**
         * 警告
         */
        WARNING,
        /**
         * 错误
         */
        ERROR,
        /**
         * 异常
         */
        EXCEPTION
    }


}
