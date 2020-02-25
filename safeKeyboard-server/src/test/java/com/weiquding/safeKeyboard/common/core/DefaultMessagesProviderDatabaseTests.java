package com.weiquding.safeKeyboard.common.core;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;
import java.util.concurrent.TimeoutException;

/**
 * DefaultMessagesProvider 测试类,基于数据库
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"base.messages.type=database"})
public class DefaultMessagesProviderDatabaseTests {

    @Autowired
    private MessagesProvider messagesProvider;

    @Test
    public void testResolveErrorWithThrowableAndLocale_zh_CN() {
        DataIntegrityViolationException th = new DataIntegrityViolationException("Exception thrown when an attempt to insert or update data results in violation of an integrity constraint. Note that this is not purely a relational concept; unique primary keys are required by most database types.");
        Object message = messagesProvider.resolveError(th, Locale.CHINA);
        Assert.assertEquals("BASEBP0001", message);
    }

    @Test
    public void testResolveErrorWithThrowableAndLocale_en() {
        DataIntegrityViolationException th = new DataIntegrityViolationException("Exception thrown when an attempt to insert or update data results in violation of an integrity constraint. Note that this is not purely a relational concept; unique primary keys are required by most database types.");
        Object message = messagesProvider.resolveError(th, Locale.ENGLISH);
        Assert.assertEquals("BASEBP0002", message);
    }

    @Test
    public void testResolveErrorWithThrowableAndLocale_en_fallback_Locale_default() {
        TimeoutException th = new TimeoutException("Exception thrown when a blocking operation times out.");
        Object message = messagesProvider.resolveError(th, Locale.ENGLISH);
        Assert.assertEquals("BASEBP0002", message);
    }

    @Test
    public void testResolveErrorWithThrowableAndLocale_en_fallback_defaultMappingCode() {
        ArrayIndexOutOfBoundsException th = new ArrayIndexOutOfBoundsException("Thrown to indicate that an array has been accessed with an illegal index. ");
        Object message = messagesProvider.resolveError(th, Locale.CHINA);
        Assert.assertEquals("未知系统异常，请联系系统管理员。", message);
    }
    @Test
    public void testResolveErrorWithErrorCodeAndLocale_zh_CN_AtServer() {
        Object message = messagesProvider.resolveError(BaseBPError.TIMEOUT.getInfo().getCode(), null, BaseBPError.TIMEOUT.getInfo().getDefaultMsg(), Locale.CHINA);
        Assert.assertEquals("系统忙，请检查细节并确认结果。", message);
    }

    @Test
    public void testResolveErrorWithErrorCodeAndLocale_zh_CN_fallback_common() {
        Object message = messagesProvider.resolveError(BaseBPError.UNKNOWN.getInfo().getCode(), null, BaseBPError.UNKNOWN.getInfo().getDefaultMsg(), Locale.CHINA);
        Assert.assertEquals("未知系统异常，请联系系统管理员。", message);
    }

    @Test
    public void testResolveErrorWithErrorCodeAndLocale_en_AtServer() {
        Object message = messagesProvider.resolveError(BaseBPError.TIMEOUT.getInfo().getCode(), null, BaseBPError.TIMEOUT.getInfo().getDefaultMsg(), Locale.ENGLISH);
        Assert.assertEquals("The system is busy, please check the details and confirm the result", message);
    }

    @Test
    public void testResolveErrorWithErrorCodeAndLocale_en_fallback_common() {
        Object message = messagesProvider.resolveError(BaseBPError.UNKNOWN.getInfo().getCode(), null, BaseBPError.UNKNOWN.getInfo().getDefaultMsg(), Locale.ENGLISH);
        Assert.assertEquals("Unknown system exception, please contact system administrator.", message);
    }

    @Test
    public void testResolveErrorWithErrorCodeAndLocale_zh_CN_fallback_defaultMsg() {
        Object message = messagesProvider.resolveError(BaseBPError.SPLICING_PARAMETERS.getInfo().getCode(), null, BaseBPError.SPLICING_PARAMETERS.getInfo().getDefaultMsg(), Locale.CHINA);
        Assert.assertEquals("An error occurred while splicing parameters", message);
    }

    @Test
    public void testResolveErrorWithErrorCodeAndLocale_zh_CN_with_args() {
        Object message = messagesProvider.resolveError(BaseBPError.PASSWORD_INCORRECT.getInfo().getCode(), new Object[]{1}, BaseBPError.PASSWORD_INCORRECT.getInfo().getDefaultMsg(), Locale.CHINA);
        Assert.assertEquals("您输入的密码不正确，今日还剩1次机会。", message);
    }


    @Test
    public void testGetMessageWithLocale_zh_CN() {
        Object message = messagesProvider.getMessage("loginForm.password.input", Locale.CHINA);
        Assert.assertEquals("请输入密码。", message);
    }

    @Test
    public void testGetMessageWithLocale_en() {
        Object message = messagesProvider.getMessage("loginForm.password.input", Locale.ENGLISH);
        Assert.assertEquals("Please enter your password.", message);
    }

    @Test
    public void testGetMessageWithLocale_zh_CN_fallback_defaultMsg() {
        Object message = messagesProvider.getMessage("loginForm.cert.input", null, Locale.CHINA);
        Assert.assertEquals("loginForm.cert.input", message);
    }


}
