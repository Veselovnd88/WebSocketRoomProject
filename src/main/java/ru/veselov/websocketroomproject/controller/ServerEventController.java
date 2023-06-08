package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.veselov.websocketroomproject.service.ChatEventService;

import java.security.Principal;

@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = "http://localhost:8080")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class ServerEventController {

    private final ChatEventService chatEventService;

    /**
     * Controller handling subscription from client's eventsource and return stream of events;
     * !!!Important!!!
     * Eventsource should be created together with websocket connection and completed with websocket disconnection
     * Subscription for events and chatUsers for WebSocket saving and removing together
     */
    @GetMapping(value = "/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> subscribe(@RequestParam String roomId, Principal principal) {
        return chatEventService.createEventStream(principal, roomId);
    }

}