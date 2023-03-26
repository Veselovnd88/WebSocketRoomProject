package ru.veselov.websocketroomproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.veselov.websocketroomproject.controller.EventType;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventMessageService {

    private final EmitterService emitterService;

    public void sendEventMessageToEmitters(String roomId, EventType eventType, Object message) {
        List<SseEmitter> emittersByRoomId = emitterService.findEmittersByRoomId(roomId);
        emittersByRoomId.forEach(x -> {
            try {
                x.send(SseEmitter.event().name(eventType.toString()).data(message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("Message for event {} sent to all emitters of room #{}", eventType, roomId);
    }

}
