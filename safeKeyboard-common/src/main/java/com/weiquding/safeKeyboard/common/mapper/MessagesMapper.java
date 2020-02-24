package com.weiquding.safeKeyboard.common.mapper;

import com.weiquding.safeKeyboard.common.domain.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

/**
 * 资源束查询
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/21
 */
@Mapper
public interface MessagesMapper {

    /**
     * 根据locale查询Message
     *
     * @param tableName 表名
     * @param locale    语言码
     * @return Message
     */
    @Select("select code, locale, message, createTime, updateTime from ${tableName} where locale=#{locale}")
    List<Message> selectByLocale(@Param("tableName") String tableName, @Param("locale") String locale);

    /**
     * 根据locale查询最后修改时间
     *
     * @param tableName 表名
     * @param locale    语言码
     * @return 最后修改时间
     */
    @Select("select max(updateTime) from ${tableName} where locale=#{locale}")
    Timestamp selectLastModifiedTime(@Param("tableName") String tableName, @Param("locale") String locale);
}
