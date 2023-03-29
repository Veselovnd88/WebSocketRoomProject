package ru.veselov.websocketroomproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

@Data
@AllArgsConstructor
public class SubscriptionData {
    private String username;
    private FluxSink<ServerSentEvent> fluxSink;
    private boolean isConnected;
}
