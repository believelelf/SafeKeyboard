package com.weiquding.safeKeyboard.common.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

/**
 * SKBS0001处理器
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/26
 */
@Slf4j
@Component("SKBS0001ExchangeHandler")
public class SKBS0001ExchangeHandler<R> implements ExchangeHandler<R, Result> {

    @Override
    public void handleRequestEntity(HttpEntity<R> requestEntity) {
        log.info("请求参数:{}", requestEntity);
    }

    @Override
    public void handleResponseEntity(HttpEntity<Result> responseEntity) {
        log.info("返回数据:{}", responseEntity);
    }
}
