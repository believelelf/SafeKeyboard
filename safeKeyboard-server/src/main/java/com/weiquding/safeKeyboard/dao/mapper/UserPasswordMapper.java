package com.weiquding.safeKeyboard.dao.mapper;

import com.weiquding.safeKeyboard.dao.model.UserPasswordModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户名密码操作
 * @author believeyourself
 */
@Mapper
public interface UserPasswordMapper {

    UserPasswordModel selectOneByUserId(@Param("userid") String userid);

    int updateUserPassword(UserPasswordModel userPasswordModel);

    int resetUserPassword(UserPasswordModel userPasswordModel);


}
