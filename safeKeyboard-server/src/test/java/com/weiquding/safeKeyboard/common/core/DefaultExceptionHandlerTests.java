package com.weiquding.safeKeyboard.common.core;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import com.weiquding.safeKeyboard.common.exception.ErrorDetail;
import com.weiquding.safeKeyboard.common.exception.ExceptionHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"base.messages.type=bundle"})
public class DefaultExceptionHandlerTests {

    @Autowired
    ExceptionHandler exceptionHandler;

    @Test
    public void testHandleDataIntegrityViolationExceptionWithLocale_zh_CN() {
        ErrorDetail errorDetail = exceptionHandler.handleException(new DataIntegrityViolationException("Exception thrown when an attempt to insert or update data results in violation of an integrity constraint."), Locale.CHINA);
        Assert.assertEquals(errorDetail.getCode(), "SAFE0001BP0001");
        Assert.assertEquals(errorDetail.getMessage(), "未知系统异常，请联系系统管理员。");
    }


    @Test
    public void testHandleDataIntegrityViolationExceptionWithLocale_en() {
        ErrorDetail errorDetail = exceptionHandler.handleException(new DataIntegrityViolationException("Exception thrown when an attempt to insert or update data results in violation of an integrity constraint."), Locale.ENGLISH);
        Assert.assertEquals(errorDetail.getCode(), "SAFE0001BP0002");
        Assert.assertEquals(errorDetail.getMessage(), "The system is too busy, please check the details and confirm the result");
    }

    @Test
    public void testHandleArrayIndexOutOfBoundsExceptionWithLocale_zh_CN() {
        ErrorDetail errorDetail = exceptionHandler.handleException(new ArrayIndexOutOfBoundsException("Thrown to indicate that an array has been accessed with an illegal index. "), Locale.ENGLISH);
        Assert.assertEquals(errorDetail.getCode(), "SAFE0001BP0001");
        Assert.assertEquals(errorDetail.getMessage(), "Unknown system exception, please contact system administrator.");
    }

    @Test
    public void testHandleBaseBPError_PASSWORD_INCORRECT_WithLocale_zh_CN() {
        ErrorDetail errorDetail = exceptionHandler.handleException(BaseBPError.PASSWORD_INCORRECT.getInfo().initialize(1), Locale.CHINA);
        Assert.assertEquals(errorDetail.getCode(), "SAFE0001BP0025");
        Assert.assertEquals(errorDetail.getMessage(), "您输入的密码不正确，今日还剩1次机会。");
    }

    @Test
    public void testHandleBaseBPError_PASSWORD_INCORRECT_WithLocale_en() {
        ErrorDetail errorDetail = exceptionHandler.handleException(BaseBPError.PASSWORD_INCORRECT.getInfo().initialize(1), Locale.ENGLISH);
        Assert.assertEquals(errorDetail.getCode(), "SAFE0001BP0025");
        Assert.assertEquals(errorDetail.getMessage(), "The password you entered is not correct. There are only 1 chances left today.");
    }

    @Test
    public void testHandleBaseBPError_ILLEGAL_CHARACTERS_WithLocale_zh_CN() {
        ErrorDetail errorDetail = exceptionHandler.handleException(BaseBPError.ILLEGAL_CHARACTERS.getInfo().initialize(1), Locale.ENGLISH);
        Assert.assertEquals(errorDetail.getCode(), "SAFE0001BP0027");
        Assert.assertEquals(errorDetail.getMessage(), "The password contains illegal characters");
    }


}
