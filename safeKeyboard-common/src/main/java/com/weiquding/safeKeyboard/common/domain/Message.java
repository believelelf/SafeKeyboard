package com.weiquding.safeKeyboard.common.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * 资源束
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/21
 */
@ToString
@Setter
@Getter
public class Message {

    private String code;
    private String locale;
    private String message;
    private Timestamp createTime;
    private Timestamp updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(code, message.code) &&
                Objects.equals(locale, message.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, locale);
    }
}
