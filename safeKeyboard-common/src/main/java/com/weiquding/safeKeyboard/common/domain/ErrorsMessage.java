package com.weiquding.safeKeyboard.common.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * error_messages表实体
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/19
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ErrorsMessage extends Message {

    private int id;
    private SystemCode systemCode;
    private Type type;



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

}
