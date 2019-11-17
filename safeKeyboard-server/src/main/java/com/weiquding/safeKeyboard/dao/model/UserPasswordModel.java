package com.weiquding.safeKeyboard.dao.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Data
public class UserPasswordModel {
    /**
     * 自增主键
     */
    private int id;
    /**
     * 用户ID
     */
    private String userid;
    /**
     * 交易密码
     */
    private String password;
    /**
     * 密码状态：00-状态正常；01-状态异常
     */
    private String status;
    /**
     * 密码核验日期
     */
    private Date verifydate;
    /**
     * 当日密码校验次数
     */
    private int errortimes;
    /**
     * 创建时间
     */
    private Timestamp createtime;
    /**
     * 更新时间
     */
    private Timestamp updatetime;

    /**
     * 是否状态异常
     * @return 状态
     */
    public boolean isStatusError() {
        return "01".equals(status)
                || (errortimes > 5 && lastVerifyDateIsToday());
    }

    /**
     * 最后的校验日期是否为今天
     *
     * @return 是否为今天
     */
    public boolean lastVerifyDateIsToday(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(new Date());
        return verifydate != null && currentDate.equals(dateFormat.format(verifydate));
    }


}
