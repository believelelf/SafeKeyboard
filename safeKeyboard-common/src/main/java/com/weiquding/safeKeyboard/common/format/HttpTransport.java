package com.weiquding.safeKeyboard.common.format;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import com.weiquding.safeKeyboard.common.exception.ErrorDetail;
import com.weiquding.safeKeyboard.common.exception.ResultFailException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Http请求工具类
 * <p>
 * 参数化类型知识
 * 1. Using Spring RestTemplate in generic method with generic parameter
 * https://stackoverflow.com/questions/21987295/using-spring-resttemplate-in-generic-method-with-generic-parameter
 * <p>
 * 2. Java Code Examples for sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl.make()
 * https://www.programcreek.com/java-api-examples/?class=sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl&method=make
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/14
 */
public class HttpTransport implements InitializingBean {

    private RestTemplate restTemplate;

    private ExchangeHandler exchangeHandler;

    private String rootUri;

    public HttpTransport() {
    }

    public HttpTransport(String rootUri, RestTemplate restTemplate, ExchangeHandler exchangeHandler) {
        this.restTemplate = restTemplate;
        this.exchangeHandler = exchangeHandler;
        this.rootUri = rootUri;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public ExchangeHandler getExchangeHandler() {
        return exchangeHandler;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setExchangeHandler(ExchangeHandler exchangeHandler) {
        this.exchangeHandler = exchangeHandler;
    }

    public String getRootUri() {
        return rootUri;
    }

    public void setRootUri(String rootUri) {
        this.rootUri = rootUri;
    }

    /**
     * POST请求
     *
     * @param serviceType 系统渠道
     * @param url         请求url
     * @param body        请求参数
     * @param clazz       返回类型
     * @param header      请求头参数 key1, value1, key2, value2
     * @param <T>         Type
     * @param <R>         Type
     * @return ResponseEntity
     */
    @SuppressWarnings("all")
    public <R, T> ResponseEntity<Result<T>> postForEntity(ServiceType serviceType, String url, R body, Class<T> clazz, String... header) {
        try {
            RequestEntity.BodyBuilder bodyBuilder = RequestEntity
                    .post(new URI(applyUrl(url)))
                    .contentType(MediaType.APPLICATION_JSON);
            // 添加请求头参数
            addHeaders(bodyBuilder, header);
            if (exchangeHandler != null) {
                exchangeHandler.handleRequestEntity(bodyBuilder);
            }
            RequestEntity<R> requestEntity = bodyBuilder.body(body);
            // Result<T> 参数化类型
            ResolvableType resolvableType = ResolvableType.forClassWithGenerics(Result.class, clazz);
            ParameterizedTypeReference<Result<T>> typeRef = ParameterizedTypeReference.forType(resolvableType.getType());
            // 发送请求
            ResponseEntity<Result<T>> responseEntity = restTemplate.exchange(applyUrl(url), HttpMethod.POST, requestEntity, typeRef, new Object[]{});
            if (exchangeHandler != null) {
                exchangeHandler.handleResponseEntity(responseEntity);
            }
            // 处理公共异常
            Result<T> entityBody = responseEntity.getBody();
            if (entityBody != null && !entityBody.isSuccess()) {
                ErrorDetail errorDetail = entityBody.getHead();
                throw new ResultFailException(serviceType, errorDetail.getCode(), errorDetail.getMessage(), entityBody);
            }
            return responseEntity;
        } catch (URISyntaxException e) {
            throw BaseBPError.UNKNOWN.getInfo().initialize(e);
        }
    }

    /**
     * 添加请求头参数
     *
     * @param bodyBuilder RequestEntity.BodyBuilder
     * @param header      请求头参数
     */
    private void addHeaders(RequestEntity.BodyBuilder bodyBuilder, String[] header) {
        if (header != null && header.length > 0 && header.length % 2 == 0) {
            for (int i = 0; i < header.length; i += 2) {
                bodyBuilder.header(header[i], header[i + 1]);
            }
        }
    }

    /**
     * POST请求
     *
     * @param serviceType 系统渠道
     * @param url         请求url
     * @param body        请求参数
     * @param clazz       返回类型
     * @param header      请求头参数 key1, value1, key2, value2
     * @param <T>         Type
     * @return Result
     */
    @SuppressWarnings("all")
    public <R, T> Result<T> postForObject(ServiceType serviceType, String url, R body, Class<T> clazz, String... header) {
        return postForEntity(serviceType, url, body, clazz, header).getBody();
    }

    private String applyUrl(String url) {
        if (StringUtils.startsWithIgnoreCase(url, "/")) {
            return getRootUri() + url;
        }
        return url;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(restTemplate, "RestTemplate must not be null");
        Assert.notNull(rootUri, "RootUri must not be null");
    }
}
