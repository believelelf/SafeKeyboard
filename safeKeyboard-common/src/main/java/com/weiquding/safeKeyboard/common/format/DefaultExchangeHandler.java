package com.weiquding.safeKeyboard.common.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

/**
 * 默认处理器,仅打印RequestEntity及ResponseEntity
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/26
 */
@Slf4j
public class DefaultExchangeHandler<R> implements ExchangeHandler<R> {

    @Override
    public void handleRequestEntity(RequestEntity<R> requestEntity) {
        log.info("请求参数:{}", requestEntity);
    }

    @Override
    public <T> void handleResponseEntity(ResponseEntity<Result<T>> responseEntity) {
        log.info("返回数据:{}", responseEntity);
    }
}
