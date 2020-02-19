package com.weiquding.safeKeyboard.common.dao;

import com.weiquding.safeKeyboard.common.domain.ErrorMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ErrorMessages数据库操作
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/19
 */
@Mapper
public interface ErrorMessagesMapper {

    /**
     * 查询所有ErrorMessage
     *
     * @return 所有ErrorMessage
     */
    @Select("select * from error_messages")
    List<ErrorMessage> selectAll();

    /**
     * 查询所有ErrorMessage
     *
     * @param locale 语言码
     * @return 所有ErrorMessage
     */
    @Select("select * from values_messages where locale=#{locale}")
    List<ErrorMessage> selectByLocale(@Param("locale") String locale);
}
