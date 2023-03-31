package ru.veselov.websocketroomproject.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface SSEService {
    Flux<ServerSentEvent> createEventStream(String roomId, String username);
}
