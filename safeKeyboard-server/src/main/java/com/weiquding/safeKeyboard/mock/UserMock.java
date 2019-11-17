package com.weiquding.safeKeyboard.mock;

import com.weiquding.safeKeyboard.common.cache.GuavaCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserMock {

    @PostConstruct
    public void init(){
        Map<String, String> sessionMapping = new HashMap<>();
        sessionMapping.put("testID", "11011");
        GuavaCache.SERVER_CACHE.put("sessionId", sessionMapping);
    }

    /**
     * 根据sessionId获取userId
     * @param sessionId sesssionID
     * @return userId
     */
    public String getUserIdBySessionId(String sessionId){
        Map<String, String> sessionMapping = GuavaCache.SERVER_CACHE.getIfPresent("sessionId");
        return sessionMapping.get(sessionId);
    }
}
