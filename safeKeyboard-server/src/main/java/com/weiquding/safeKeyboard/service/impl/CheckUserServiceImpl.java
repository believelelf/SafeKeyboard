package com.weiquding.safeKeyboard.service.impl;

import com.weiquding.safeKeyboard.common.exception.CipherRuntimeException;
import com.weiquding.safeKeyboard.dao.mapper.UserPasswordMapper;
import com.weiquding.safeKeyboard.dao.model.UserPasswordModel;
import com.weiquding.safeKeyboard.service.CheckUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckUserServiceImpl implements CheckUserService {

    @Autowired
    private UserPasswordMapper userPasswordMapper;

    @Override
    public boolean checkUserPassword(String userId, String hashedPassword) {
        UserPasswordModel model = userPasswordMapper.selectOneByUserId(userId);
        if (model.isStatusError()) {
            throw new CipherRuntimeException("Your account has been locked, please try again tomorrow.");
        }
        if (hashedPassword.equals(model.getPassword())) {
            // 密码比较成功
            model.setErrortimes(0);
            userPasswordMapper.resetUserPassword(model);
        } else if (model.lastVerifyDateIsToday()) {
            //  密码比较不成功，且当日已经校验过
            userPasswordMapper.updateUserPassword(model);
            throw new CipherRuntimeException(
                    String.format("The password you entered is incorrect, %d chances are left", 5 - model.getErrortimes())
            );
        } else {
            //  密码比较不成功，且当日为初次校验
            model.setErrortimes(1);
            userPasswordMapper.updateUserPassword(model);
            throw new CipherRuntimeException(
                    String.format("The password you entered is incorrect, %d chances are left", 5)
            );
        }
        return true;
    }

    @Override
    public boolean checkPasswordRule(String plainPassword) {
        char[] pwds = plainPassword.toCharArray();
        if (pwds.length != 6) {
            throw new CipherRuntimeException("Incorrect password length");
        }
        int count = 0;
        char last = 'c';
        for (char c : pwds) {
            if (c > '9' || c < '0') {
                throw new CipherRuntimeException("The password contains illegal characters");
            }
            if (c == last || c == last + 1) {
                count++;
            }
            last = c;
        }
        if (count == 5) {
            throw new CipherRuntimeException("The password consists entirely of equal characters or incrementing sequences");
        }
        return true;
    }
}
