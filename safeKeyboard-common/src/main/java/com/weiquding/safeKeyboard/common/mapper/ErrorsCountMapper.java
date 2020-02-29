package com.weiquding.safeKeyboard.common.mapper;

import com.weiquding.safeKeyboard.common.exception.ErrorDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 错误信息统计数据操作
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/1
 */
@Mapper
public interface ErrorsCountMapper {

    /**
     * 插入错误信息
     * @param errorDetail 错误信息实体
     * @return 是否成功
     */
    @Insert("insert errors_count" +
            "(traceNo, code, message, cause, hostName, helpLink, path, refersTo, severity, createtime)" +
            " values" +
            "(" +
            " #{id}," +
            " #{code}," +
            " #{message}," +
            " #{cause}," +
            " #{hostName}," +
            " #{helpLink}," +
            " #{path}," +
            " #{refersTo}," +
            " #{severity}," +
            " #{timestamp}" +
            ")")
    int insertErrorDetail(ErrorDetail errorDetail);
}
