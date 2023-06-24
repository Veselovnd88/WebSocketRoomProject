package ru.veselov.websocketroomproject.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = "fluxSink")
public class SubscriptionData {

    private String username;

    private String roomId;

    private FluxSink<ServerSentEvent> fluxSink;

}
