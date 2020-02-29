package com.weiquding.safeKeyboard.common.format;

import com.weiquding.safeKeyboard.common.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * 默认处理器,仅添加x-traceNo并打印ResponseEntity
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/26
 */
@Slf4j
public class DefaultExchangeHandler implements ExchangeHandler {

    @Override
    public void handleRequestEntity(RequestEntity.BodyBuilder bodyBuilder) {
        bodyBuilder.header(Constants.TRACE_NO, UUID.randomUUID().toString());
    }

    @Override
    public <T> void handleResponseEntity(ResponseEntity<Result<T>> responseEntity) {
        log.info("返回数据:{}", responseEntity);
    }
}
