package com.weiquding.safeKeyboard.common.dao;

import com.weiquding.safeKeyboard.common.domain.ValuesMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ValuesMessages数据库操作
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/19
 */
@Mapper
public interface ValuesMessagesMapper {

    /**
     * 查询所有ValuesMessage
     *
     * @return 所有ValuesMessage
     */
    @Select("select * from values_messages")
    List<ValuesMessage> selectAll();

    /**
     * 查询所有ValuesMessage
     * @param locale 语言码
     * @return 所有ValuesMessage
     */
    @Select("select * from values_messages where locale=#{locale}")
    List<ValuesMessage> selectByLocale(@Param("locale") String locale);
}
