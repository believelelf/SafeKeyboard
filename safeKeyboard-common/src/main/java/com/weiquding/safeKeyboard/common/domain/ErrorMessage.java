package com.weiquding.safeKeyboard.common.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * error_messages表实体
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/19
 */
@Getter
@Setter
@ToString
public class ErrorMessage {

    private int id;
    private String code;
    private String locale;
    private String message;
    private SystemCode systemCode;
    private Type type;
    private Timestamp createTime;
    private Timestamp updateTime;


    public enum SystemCode {
        /**
         * BASE
         */
        BASE,
        /**
         * 安全系统
         */
        SAFE,
        ;
    }

    public enum Type {
        /**
         * 业务类
         */
        BIZ,
        /**
         * 系统类
         */
        SYSTEM,
        ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorMessage that = (ErrorMessage) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, locale);
    }
}
