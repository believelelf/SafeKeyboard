package com.weiquding.safeKeyboard.service;

public interface CheckUserService {

    /**
     * 校验用户密码
     * @param userId 用户id
     * @param hashedPassword 密码哈希
     * @return 是否符合
     */
    boolean checkUserPassword(String userId, String hashedPassword);

    /**
     * 校验密码规则
     * @param plainPassword 明文密码
     * @return 规则
     */
    boolean checkPasswordRule(String plainPassword);
}
