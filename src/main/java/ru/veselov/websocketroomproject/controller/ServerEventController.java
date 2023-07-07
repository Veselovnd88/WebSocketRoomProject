package ru.veselov.websocketroomproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.service.ChatEventService;

import java.security.Principal;

@RestController
@RequestMapping("/api/room")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "EventSource controller", description = "Managing subscriptions of EventSources")
public class ServerEventController {

    private final ChatEventService chatEventService;

    /**
     * Controller handling subscription from client's eventsource and return stream of events;
     * !!!Important!!!
     * Eventsource should be created together with websocket connection and completed with websocket disconnection
     * Subscription for events and chatUsers for WebSocket saving and removing together
     */
    @Operation(summary = "EventSource subscriptions", description = "Return Event Stream")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(schema = @Schema(implementation = EventMessageDTO.class),
                    mediaType = MediaType.TEXT_EVENT_STREAM_VALUE))
    @GetMapping(value = "/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> subscribe(@Parameter(in = ParameterIn.QUERY, description = "Room ID as UUID", required = true,
            example = "1bd7c828-3a5c-4fd9-a2af-78b6a127459f")
                                           @RequestParam String roomId, Principal principal) {
        return chatEventService.createEventStream(principal, roomId);
    }

}
