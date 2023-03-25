package ru.veselov.websocketroomproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SubscriptionSSEService {

    private final Map<String, CopyOnWriteArrayList<SseEmitter>> roomIdEmitterMap = new ConcurrentHashMap<>();

    public void saveSubscription(String roomId, SseEmitter sseEmitter) {
        if (roomIdEmitterMap.containsKey(roomId)) {
            roomIdEmitterMap.get(roomId).add(sseEmitter);
        } else {
            CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
            emitters.add(sseEmitter);
            roomIdEmitterMap.put(roomId, emitters);
        }
        log.info("New client added to room #{}", roomId);
    }

    public void removeEmitter(String roomId, SseEmitter sseEmitter) {
        roomIdEmitterMap.get(roomId).remove(sseEmitter);
        log.info("Client removed from room #{}", roomId);
    }

}
