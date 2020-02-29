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
public class SubmitRsp {

    private String account;
    private String toAccount;
    private String tranAmount;

    public SubmitRsp(String account, String toAccount, String tranAmount) {
        this.account = account;
        this.toAccount = toAccount;
        this.tranAmount = tranAmount;
    }

    public SubmitRsp() {
    }

    public SubmitRsp(SubmitReq req) {
        this.account = req.getAccount();
        this.toAccount = req.getToAccount();
        this.tranAmount = req.getTranAmount();
    }

}
