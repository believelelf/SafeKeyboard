package com.weiquding.safeKeyboard.common.support;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 数据库MessageSource资源加载。
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/21
 */
public interface DatabaseMessageLoader {

    /**
     * 从数据库加载资源.
     *
     * @param tableName 表名
     * @param locale    Locale字符串
     *                  language_country_variant, language_country, language
     *                  Locale "de_AT_oo" -> "de_AT_OO", de_AT", "de".
     * @return code --> message
     */
    Map<String, String> loadMessageSource(String tableName, String locale);

    /**
     * 从数据库加载资源的最后修改时间
     * @param tableName 表名
     * @param locale    Locale字符串
     * @return 最后修改时间
     */
    Timestamp lastModified(String tableName, String locale);
}
