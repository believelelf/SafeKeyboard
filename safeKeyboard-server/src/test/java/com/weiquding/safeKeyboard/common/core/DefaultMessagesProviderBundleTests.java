package com.weiquding.safeKeyboard.common.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

/**
 * DefaultMessagesProvider 测试类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"base.messages.type=bundle"})
public class DefaultMessagesProviderBundleTests {

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

}
