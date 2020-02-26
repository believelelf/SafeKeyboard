package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import com.weiquding.safeKeyboard.common.exception.ErrorDetail;
import com.weiquding.safeKeyboard.common.exception.ErrorInfo;
import com.weiquding.safeKeyboard.common.exception.ResultFailException;
import com.weiquding.safeKeyboard.common.format.ExchangeHandler;
import com.weiquding.safeKeyboard.common.format.Result;
import com.weiquding.safeKeyboard.common.format.ServiceType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

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
@Component
public class HttpUtil {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * ExchangeHandler集合，Key --> {@link com.weiquding.safeKeyboard.common.format.ServiceType}
     */
    @Autowired(required = false)
    private Map<String, ExchangeHandler> exchangeHandlerMap;


    // TODO 4. 服务端报文返回报文修改
    // TODO 5. 切面修改


    /**
     * Execute the HTTP method to the given URI template, writing the given
     * request entity to the request, and returns the response as {@link ResponseEntity}.
     * The given {@link ParameterizedTypeReference} is used to pass generic type information:
     * <pre class="code">
     * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean =
     *     new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {};
     *
     * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response =
     *     template.exchange(&quot;https://example.com&quot;,HttpMethod.GET, null, myBean);
     * </pre>
     *
     * @param url           the URL
     * @param method        the HTTP method (GET, POST, etc)
     * @param requestEntity the entity (headers and/or body) to write to the
     *                      request (may be {@code null})
     * @param responseType  the type of the return value
     * @param uriVariables  the variables to expand in the template
     * @return the response as entity
     */
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
                                          ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    @SuppressWarnings("unchecked")
    public <T, R> ResponseEntity<Result<T>> postForEntity(ServiceType serviceType, R body, String url, Class<T> clazz) {
        try {
            RequestEntity requestEntity = RequestEntity
                    .post(new URI(url))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(Constants.TRACE_NO, UUID.randomUUID().toString())
                    .body(body);
            ExchangeHandler exchangeHandler = exchangeHandlerMap.get(serviceType.getExchangeHandler());
            if (exchangeHandler != null) {
                exchangeHandler.handleRequestEntity(requestEntity);
            }
            // Result<T> 参数化类型
            ResolvableType resolvableType = ResolvableType.forClassWithGenerics(Result.class, clazz);
            ParameterizedTypeReference<Result<T>> typeRef = ParameterizedTypeReference.forType(resolvableType.getType());
            // 发送请求
            ResponseEntity<Result<T>> responseEntity = exchange(url, HttpMethod.POST, requestEntity, typeRef, null);
            if (exchangeHandler != null) {
                exchangeHandler.handleResponseEntity(responseEntity);
            }
            Class resultFailException = serviceType.getResultFailException();
            resultFailException = resultFailException == null ? ResultFailException.class : resultFailException;
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                throw (ResultFailException) BeanUtils.instantiateClass(resultFailException.getConstructor(ErrorInfo.class, Object.class), BaseBPError.UNKNOWN.getInfo(), responseEntity);
            }
            Result<T> entityBody = responseEntity.getBody();
            if (entityBody != null && !entityBody.isSuccess()) {
                ErrorDetail errorDetail = entityBody.getHead();
                throw (ResultFailException) BeanUtils.instantiateClass(resultFailException.getConstructor(String.class, String.class, Object.class), errorDetail.getCode(), errorDetail.getMessage(), entityBody);
            }
            return responseEntity;
        } catch (URISyntaxException | NoSuchMethodException e) {
            throw BaseBPError.UNKNOWN.getInfo().initialize(e);
        }
    }


}
