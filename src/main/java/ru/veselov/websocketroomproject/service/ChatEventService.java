package ru.veselov.websocketroomproject.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.security.Principal;

public interface ChatEventService {
    Flux<ServerSentEvent> createEventStream(Principal principal, String roomId);
}
