package ru.veselov.websocketroomproject.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketDisconnectListener {

    private final ChatUserService chatUserService;

    private final EventMessageService eventMessageService;

    private final RoomSubscriptionService roomSubscriptionService;

    @EventListener
    public void handleUserDisconnect(SessionDisconnectEvent session) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(session.getMessage());
        String sessionId = stompHeaderAccessor.getSessionId();
        ChatUser chatUser = chatUserService.removeChatUser(sessionId);
        eventMessageService.sendUserDisconnectedMessage(chatUser);
        getFluxSink(chatUser).complete();
        log.info("User {} is disconnected", chatUser.getUsername());
    }

    private FluxSink<ServerSentEvent> getFluxSink(ChatUser chatUser) {
        return roomSubscriptionService.findSubscription(chatUser.getRoomId(), chatUser.getUsername()).getFluxSink();
    }

}
