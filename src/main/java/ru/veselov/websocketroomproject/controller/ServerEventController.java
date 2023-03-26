package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.veselov.websocketroomproject.exception.EmitterIOException;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EmitterService;
import ru.veselov.websocketroomproject.service.EventMessageService;

import java.util.Set;

@RestController
@RequestMapping("/api/room")
@Slf4j
@RequiredArgsConstructor
public class ServerEventController {

    private final EmitterService emitterService;

    private final EventMessageService eventMessageService;

    private final ChatUserService chatUserService;

    /**
     * Controller handling subscription from client, create emitter and put it to
     * the storage;
     * Once client connected to the source, we sent list of users;
     */
    @GetMapping(value = "/sse")
    public SseEmitter subscribe(@RequestParam String roomId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        emitterService.saveEmitter(roomId, sseEmitter);
        sendUserListAfterConnection(roomId);
        sseEmitter.onCompletion(() -> emitterService.removeEmitter(roomId, sseEmitter));
        sseEmitter.onTimeout(() -> {
                    sseEmitter.complete();
                    emitterService.removeEmitter(roomId, sseEmitter);
                }
        );
        sseEmitter.onError(e -> {
                    emitterService.removeEmitter(roomId, sseEmitter);
                    log.warn("IOException of SSEEmitter: {}", e.getMessage());
                }
        );
        return sseEmitter;
    }

    private void sendUserListAfterConnection(String roomId) {
        Set<ChatUser> chatUsersByRoomId = chatUserService.findChatUsersByRoomId(roomId);
        eventMessageService.sendEventMessageToEmitters(roomId, EventType.USERS_REFRESHED, chatUsersByRoomId);
    }


}
