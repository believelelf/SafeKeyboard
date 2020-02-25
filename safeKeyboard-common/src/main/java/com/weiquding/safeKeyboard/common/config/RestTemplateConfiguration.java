package com.weiquding.safeKeyboard.common.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * RestTemplate
 *
 * @author believeyourself
 */
@Configuration
public class RestTemplateConfiguration {

    /**
     * 连接超时 - 连接超时时间，毫秒
     * Set the underlying URLConnection's connect timeout (in milliseconds).
     * A timeout value of 0 specifies an infinite timeout.
     */
    private static final int CONNECT_TIMEOUT = 2000;

    /**
     * 数据读取超时时间，即SocketTimeout - 读写超时时间，毫秒
     * Set the underlying URLConnection's read timeout (in milliseconds).
     * A timeout value of 0 specifies an infinite timeout.
     */
    private static final int SOCKET_TIMEOUT = 5000;

    /**
     * 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
     */
    private static final int CONNECTION_REQUEST_TIMEOUT = 200;

    /**
     * maximum total connection value.
     * 连接池大小
     */
    private static final int MAX_CONNECT_TOTAL = 500;

    /**
     * maximum connection per route value.
     * 每个路由的最大连接数
     */
    private static final int MAX_CONNECT_PER_ROUTE = 100;
    /**
     * 持久连接最大空间时间
     * maximum time persistent connections can stay idle while kept alive
     * in the connection pool. Connections whose inactivity period exceeds this value will
     * get closed and evicted from the pool.
     */
    private static final int MAX_IDLE_TIME = 180;

    @Value("${base.http.enableProxy}")
    private boolean enableProxy;
    @Value("${kht.http.proxy.hostname}")
    private String proxyHost;
    @Value("${kht.http.proxy.port}")
    private int proxyPost;

    /**
     * {@link org.apache.http.client.HttpClient} --> {@link org.springframework.http.client.HttpComponentsClientHttpRequestFactory}
     * 引入HttpClient，增加重试、超时、重定向、保活、连接池等特性
     *
     * @param builder RestTemplateBuilder
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();

        // 增加读取超时时间、连接超时时间、连接获取超时时间
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);

        // 增加HTTP代理
        if (enableProxy) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPost);
            requestConfigBuilder.setProxy(proxy);
        }

        /*
         * 1. 默认已增加重试机制： DefaultHttpRequestRetryHandler.INSTANCE
         *
         * Create the request retry handler with a retry count of 3, requestSentRetryEnabled false
         * and using the following list of non-retriable IOException classes: <br>
         * <ul>
         * <li>InterruptedIOException</li>
         * <li>UnknownHostException</li>
         * <li>ConnectException</li>
         * <li>SSLException</li>
         *
         * 2. 默认已增加连接保活机制：DefaultConnectionKeepAliveStrategy.INSTANCE
         * Default implementation of a strategy deciding duration
         * that a connection can remain idle.
         *
         * The default implementation looks solely at the 'Keep-Alive'
         * header's timeout token.
         *
         * 3. 使用重定向机制：LaxRedirectStrategy.INSTANCE
         * Lax {@link org.apache.http.client.RedirectStrategy} implementation
         * that automatically redirects all HEAD, GET, POST, and DELETE requests.
         * This strategy relaxes restrictions on automatic redirection of
         * POST methods imposed by the HTTP specification.
         *
         */

        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(requestConfigBuilder.build())
                // 连接池管理器PoolingHttpClientConnectionManager，设置相关参数
                .setMaxConnTotal(MAX_CONNECT_TOTAL)
                .setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE)
                .setRedirectStrategy(LaxRedirectStrategy.INSTANCE)
                .evictExpiredConnections()
                .evictIdleConnections(MAX_IDLE_TIME, TimeUnit.SECONDS)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }
}
