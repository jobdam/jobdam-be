package com.jobdam.jobdam_be.websokect.sessionTracker.registry;

import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//tracker관리를 위해 만든 설정클래스
//채팅,시그널링 등의 여러 웹소켓연결을 구분하기 위해
//빈으로 미리 구분(클라이언트와 connect할때 구분할)값 세팅하고 구분한다
@Configuration
public class SessionTrackerRegistry {
    private final Map<String, WebSocketSessionTracker> sessionTrackers = new HashMap<>();

    @Autowired
    public SessionTrackerRegistry(Map<String, WebSocketSessionTracker> beans) {
        this.sessionTrackers.putAll(beans);
    }
    public boolean checkKey(String purpose) {
        return sessionTrackers.containsKey(purpose);
    }

    public WebSocketSessionTracker getTracker(String purpose) {
        return sessionTrackers.get(purpose);
    }

    public Collection<WebSocketSessionTracker> getAllTrackers() {
        return sessionTrackers.values();
    }

}
