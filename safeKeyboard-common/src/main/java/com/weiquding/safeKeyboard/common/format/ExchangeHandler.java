package com.weiquding.safeKeyboard.common.format;

import org.springframework.http.HttpEntity;

/**
 * Http请求处理：前置或后置
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/26
 */
public interface ExchangeHandler<REQ, RSP> {

    /**
     * 针对特定渠道，处理请求参数
     *
     * @param requestEntity 请求参数
     */
    void handleRequestEntity(HttpEntity<REQ> requestEntity);

    /**
     * 针对特定渠道，处理返回数据
     *
     * @param responseEntity 返回数据
     */
    void handleResponseEntity(HttpEntity<RSP> responseEntity);
}
