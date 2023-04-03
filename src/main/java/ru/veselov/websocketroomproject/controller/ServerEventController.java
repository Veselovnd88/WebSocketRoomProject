package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.veselov.websocketroomproject.service.ChatEventService;

@RestController
@RequestMapping("/api/room")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class ServerEventController {

    private final ChatEventService chatEventService;

    /**
     * Controller handling subscription from client's eventsource and return stream of events;
     * Eventsource should be created together with websocket connection and completed with websocket disconnection
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> subscribe(@RequestParam String roomId, Authentication authentication) {
        String username = authentication.getName();
        return chatEventService.createEventStream(username, roomId);
    }

}