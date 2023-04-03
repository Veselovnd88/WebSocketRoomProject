package ru.veselov.websocketroomproject.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface ChatEventService {
    Flux<ServerSentEvent> createEventStream(String username, String roomId);
}
