package com.weiquding.safeKeyboard.dto;

import lombok.Data;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/29
 */
@Data
public class ConfirmRsp {

    private String account;
    private String toAccount;
    private String tranAmount;
    private String safeFields;

    public ConfirmRsp(String account, String toAccount, String tranAmount) {
        this.account = account;
        this.toAccount = toAccount;
        this.tranAmount = tranAmount;
    }

    public ConfirmRsp() {
    }
}
