package com.weiquding.safeKeyboard.common.format;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

/**
 * Http请求处理：前置或后置
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/26
 */
public interface ExchangeHandler {

    /**
     * 针对特定渠道，处理请求参数
     *
     * @param bodyBuilder 请求参数Builder
     */
    void handleRequestEntity(RequestEntity.BodyBuilder bodyBuilder);

    /**
     * 针对特定渠道，处理返回数据
     *
     * @param responseEntity 返回数据
     */
    <T> void handleResponseEntity(ResponseEntity<Result<T>> responseEntity);

}
