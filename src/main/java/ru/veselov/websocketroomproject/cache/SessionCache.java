package ru.veselov.websocketroomproject.cache;

public interface SessionCache extends Cache{

    void addSessionId(String sessionId,Integer roomId);
    void removeSessionId(String sessionId);
    Integer getRoom(String sessionId);
}
