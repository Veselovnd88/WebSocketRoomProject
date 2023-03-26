package ru.veselov.websocketroomproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class EmitterService {

    private final Map<String, CopyOnWriteArrayList<SseEmitter>> roomIdEmitterMap = new ConcurrentHashMap<>();

    public void saveEmitter(String roomId, SseEmitter sseEmitter) {
        if (roomIdEmitterMap.containsKey(roomId)) {
            roomIdEmitterMap.get(roomId).add(sseEmitter);
        } else {
            CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
            emitters.add(sseEmitter);
            roomIdEmitterMap.put(roomId, emitters);
        }
        log.info("New emitter was added to room #{}", roomId);
    }

    public void removeEmitter(String roomId, SseEmitter sseEmitter) {
        roomIdEmitterMap.get(roomId).remove(sseEmitter);
        log.info("Emitter was removed from room #{}", roomId);
    }

    public List<SseEmitter> findEmittersByRoomId(String roomId) {
        return roomIdEmitterMap.get(roomId);
    }

}
