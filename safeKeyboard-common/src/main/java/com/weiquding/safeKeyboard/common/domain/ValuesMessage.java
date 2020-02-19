package com.weiquding.safeKeyboard.common.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * values_messages表实体
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/19
 */
@Getter
@Setter
@ToString
public class ValuesMessage {

    private int id;
    private String code;
    private String locale;
    private String message;
    private Timestamp createTime;
    private Timestamp updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValuesMessage that = (ValuesMessage) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, locale);
    }
}
