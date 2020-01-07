package com.weiquding.safeKeyboard.advice;

import com.weiquding.safeKeyboard.common.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * 通知
 *
 * @author wuby
 * @version V1.0
 * @date 2020/1/3
 */
@ControllerAdvice(basePackages = "com.weiquding.safeKeyboard")
public class ClientControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientControllerAdvice.class);


    @ModelAttribute
    public void handleHttpRequest(Model model, HttpServletRequest httpServletRequest) {
        model.addAttribute(Constants.REQUEST_URI, httpServletRequest.getRequestURI());
    }

}
