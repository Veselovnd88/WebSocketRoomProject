package ru.veselov.websocketroomproject.cache.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.cache.SessionCache;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SessionCacheImpl implements SessionCache {
    private final Map<String,Integer> sessionRoomCache = new HashMap<>();
    @Override
    public void addSessionId(String sessionId, Integer roomId) {
        log.trace("Room #{} added to session #{}", roomId,sessionId);
        sessionRoomCache.put(sessionId,roomId);
    }

    @Override
    public void removeSessionId(String sessionId) {
        log.trace("Session #{} removed from cache",sessionId);
        sessionRoomCache.remove(sessionId);
    }

    @Override
    public Integer getRoom(String sessionId) {
        return sessionRoomCache.get(sessionId);
    }

    @Override
    public void clear() {
        sessionRoomCache.clear();
    }
}
