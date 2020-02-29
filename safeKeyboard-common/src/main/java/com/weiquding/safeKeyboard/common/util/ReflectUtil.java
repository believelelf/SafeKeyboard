package com.weiquding.safeKeyboard.common.util;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 字段操作工具类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/29
 */
public class ReflectUtil {

    /**
     * 设置字段值
     *
     * @param data  数据对象
     * @param field 字段名称
     * @param value 字段值
     */
    @SuppressWarnings("all")
    public static void setSafeFieldValue(Object data, String field, Object... value) {
        try {
            if (data instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) data;
                map.put(field, value[0]);
                return;
            }
            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(data.getClass(), field);
            if (propertyDescriptor != null) {
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (writeMethod == null) {
                    throw new IllegalStateException(String.format("WriteMethod [%s] must not be null", field));
                }
                writeMethod.invoke(data, value);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("An error occurred while writing value to data", e);
        }
    }

    /**
     * 移除字段值
     *
     * @param data  数据对象
     * @param field 字段名
     */
    public static void removeFieldValue(Object data, String field) {
        if (data instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) data;
            map.remove(field);
            return;
        }
        setSafeFieldValue(data, field, new Object[]{null});
    }

    /**
     * 获取字段值
     *
     * @param data  数据对象
     * @param field 字段名
     * @return 字段值
     */
    @SuppressWarnings("unchecked")
    public static Object getFieldValue(Object data, String field) {
        if (data instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) data;
            return map.get(field);
        }
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(data.getClass(), field);
        if (propertyDescriptor != null) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod == null) {
                throw new IllegalStateException(String.format("ReadMethod [%s] must not be null", field));
            }
            try {
                return readMethod.invoke(data);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("An error occurred while reading value from data", e);
            }
        }
        return null;
    }
}
