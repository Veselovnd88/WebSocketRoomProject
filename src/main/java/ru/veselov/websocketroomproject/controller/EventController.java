package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.veselov.websocketroomproject.service.EventHandler;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final EventHandler eventHandler;

    @GetMapping(value = "/sse/{roomId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> openConnection(@PathVariable("roomId") String roomId) {
        ServerSentEvent<String>  event = ServerSentEvent.<String>builder()
                .data("vasya")
                .event("event").build();
        return Flux.create(flux-> flux.next(event));
    }


}
