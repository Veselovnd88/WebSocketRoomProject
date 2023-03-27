package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.EventMessageDTO;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.SubscriptionService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room")
@Slf4j
@RequiredArgsConstructor
public class ServerEventController {

    private final SubscriptionService subscriptionService;

    private final EventMessageService eventMessageService;

    private final ChatUserService chatUserService;
    private final ChatUserMapper chatUserMapper;

    /**
     * Controller handling subscription from client, create fluxsink and put it to the storage;
     * Once client connected to the source, we sent list of users;
     */
    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> subscribe(@RequestParam String roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return Flux.create(fluxSink -> {
                    log.info("Subscription for user {} of room {} created", username, roomId);
                    fluxSink.onCancel(
                            () -> {
                                subscriptionService.removeSubscription(roomId, username);
                                log.info("Subscription of user {} of room {} removed", username, roomId);
                            }
                    );
                    subscriptionService.saveSubscription(roomId, username, fluxSink);
                    eventMessageService.sendUserList(roomId);
                }
        );
    }

}
