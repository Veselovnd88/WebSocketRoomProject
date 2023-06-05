package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.veselov.websocketroomproject.security.jwt.JwtParser;
import ru.veselov.websocketroomproject.service.ChatEventService;

@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = "http://localhost:8080")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class ServerEventController {

    private final ChatEventService chatEventService;

    private final JwtParser jwtParser;

    /**
     * Controller handling subscription from client's eventsource and return stream of events;
     * Eventsource should be created together with websocket connection and completed with websocket disconnection
     * Username should be parsed directly from jwt, because of Reactive stream
     */
    @GetMapping(value = "/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> subscribe(@RequestParam String roomId, @RequestHeader("Authorization") String header) {
        String username = jwtParser.getUsername(header.substring(7));
        return chatEventService.createEventStream(username, roomId);
    }

}
