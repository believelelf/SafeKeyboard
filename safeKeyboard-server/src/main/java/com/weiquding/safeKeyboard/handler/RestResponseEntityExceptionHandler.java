package com.weiquding.safeKeyboard.handler;

import com.weiquding.safeKeyboard.common.exception.TranFailException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {TranFailException.class})
    protected ResponseEntity<Object> handleCipherException(
            RuntimeException ex, WebRequest request) {
        Map<String, String> multiValueMap = new HashMap<>();
        multiValueMap.put("errorCode", "700");
        multiValueMap.put("errorMsg", ex.getMessage());
        return handleExceptionInternal(ex, multiValueMap,
                new HttpHeaders(), HttpStatus.OK, request);
    }
}