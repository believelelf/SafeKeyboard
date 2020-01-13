package com.weiquding.safeKeyboard.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json工具类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/13
 */
public class JsonUtil {

    private JsonUtil(){}

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // Don't throw an exception when json has extra fields you are
        // not serializing on. This is useful when you want to use a pojo
        // for deserialization and only care about a portion of the json
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    }

    public static ObjectMapper getObjectMapper(){
        return OBJECT_MAPPER;
    }




}
