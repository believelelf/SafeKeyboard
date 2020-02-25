package com.weiquding.safeKeyboard.common.support;

import com.weiquding.safeKeyboard.common.mapper.MessagesMapper;
import com.weiquding.safeKeyboard.common.domain.Message;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/21
 */
@Slf4j
public class DefaultDatabaseMessageLoader implements DatabaseMessageLoader {

    private MessagesMapper messagesMapper;

    public DefaultDatabaseMessageLoader(MessagesMapper messagesMapper) {
        this.messagesMapper = messagesMapper;
    }

    @Override
    public Map<String, String> loadMessageSource(String tableName, String locale) {
        try {
            List<Message> messages = messagesMapper.selectByLocale(tableName, locale);
            Map<String, String> map = new ConcurrentHashMap<>(messages.size());
            for (Message message : messages) {
                map.put(message.getCode(), message.getMessage());
            }
            return map;
        } catch (Exception e) {
            log.warn("Failed to load MessageSource: [{}] [{}]", tableName, locale, e);
            return null;
        }
    }

    @Override
    public Timestamp lastModified(String tableName, String locale) {
        try {
            return messagesMapper.selectLastModifiedTime(tableName, locale);
        } catch (Exception e) {
            log.warn("Failed to select LastModified: [{}] [{}]", tableName, locale, e);
            return null;
        }
    }
}
