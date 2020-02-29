package com.weiquding.safeKeyboard.common.exception;

import com.weiquding.safeKeyboard.common.format.Result;
import com.weiquding.safeKeyboard.common.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Error Handling for REST with Spring
 * https://www.baeldung.com/exception-handling-for-rest-with-spring
 * <p>
 * How does spring manage ExceptionHandler priority?
 * https://stackoverflow.com/questions/48725082/how-does-spring-manage-exceptionhandler-priority
 * <p>
 * 全局异常处理
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/26
 */
@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler {


    @Autowired
    private com.weiquding.safeKeyboard.common.exception.ExceptionHandler exceptionHandler;


    @ExceptionHandler({Exception.class})
    public final ResponseEntity<Result> handleGlobalException(Exception ex, WebRequest request) throws Exception {
        ErrorDetail errorDetail = exceptionHandler.handleException(ex, request.getLocale());
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            HttpServletRequest httpServletRequest = servletWebRequest.getRequest();
            String traceNo = httpServletRequest.getHeader(Constants.TRACE_NO);
            if (StringUtils.hasText(traceNo)) {
                errorDetail.setId(traceNo);
            }
            errorDetail.setPath(httpServletRequest.getRequestURI());
        }
        logError(errorDetail, ex);
        return new ResponseEntity<>(Result.fail(errorDetail), new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * 打印异常信息
     *
     * @param errorDetail 错误信息
     * @param ex          异常信息
     */
    protected void logError(ErrorDetail errorDetail, Exception ex) {
        if (ex instanceof BaseRuntimeException) {
            BaseRuntimeException baseRuntimeException = (BaseRuntimeException) ex;
            if (baseRuntimeException.isAlreadyLogged()) {
                return;
            }
        }
        log.error("An exception occurred on the request: [{}]", errorDetail, ex);
    }

}
