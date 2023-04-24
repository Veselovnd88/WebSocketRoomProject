package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.veselov.websocketroomproject.security.JWTUtils;
import ru.veselov.websocketroomproject.service.ChatEventService;

@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = "http://localhost:8080")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class ServerEventController {

    private final ChatEventService chatEventService;

    private final JWTUtils jwtUtils;

    /**
     * Controller handling subscription from client's eventsource and return stream of events;
     * Eventsource should be created together with websocket connection and completed with websocket disconnection
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> subscribe(@RequestParam String roomId,
                                           @RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7);
        String username = jwtUtils.getUsername(jwt);
        return chatEventService.createEventStream(username, roomId);
    }

}