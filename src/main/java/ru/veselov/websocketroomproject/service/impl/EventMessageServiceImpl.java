package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.controller.EventType;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.EventMessageDTO;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.model.SubscriptionData;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class EventMessageServiceImpl implements EventMessageService {

    private final SubscriptionServiceImpl subscriptionService;

    private final ChatUserService chatUserService;

    private final ChatUserMapper chatUserMapper;

    @Override
    public void sendUserListToAllSubscriptions(String roomId) {
        sendEventMessageToSubscriptions(roomId, createUsersRefreshedEvent(roomId));
    }

    @Override
    public void sendUserConnectedMessage(ChatUser chatUser) {
        EventMessageDTO<ChatUserDTO> eventMessageDTO = new EventMessageDTO<>(
                EventType.CONNECTED,
                toChatUserDTO(chatUser));
        sendEventMessageToSubscriptions(chatUser.getRoomId(), eventMessageDTO);
    }

    @Override
    public void sendUserDisconnectedMessage(ChatUser chatUser) {
        EventMessageDTO<ChatUserDTO> eventMessageDTO = new EventMessageDTO<>(
                EventType.DISCONNECTED,
                toChatUserDTO(chatUser));
        sendEventMessageToSubscriptions(chatUser.getRoomId(), eventMessageDTO);
    }

    @Override
    public void sendUserListToSubscription(String roomId, String username) {
        sendEventMessageToOneSubscription(roomId,
                username,
                createUsersRefreshedEvent(roomId));
    }

    private void sendEventMessageToSubscriptions(String roomId, EventMessageDTO eventMessageDTO) {
        List<SubscriptionData> subscriptionsByRoomId = subscriptionService.findSubscriptionsByRoomId(roomId);
        if (subscriptionsByRoomId == null) {
            return;
        }
        EventType eventType = eventMessageDTO.getEventType();
        ServerSentEvent event = ServerSentEvent.builder()
                .data(eventMessageDTO)
                .event(eventType.name())
                .build();
        subscriptionsByRoomId.forEach(x -> x.getFluxSink().next(event));
        log.info("Message for event {} sent to all connected subscriptions of room #{}", eventType, roomId);
    }

    private void sendEventMessageToOneSubscription(String roomId, String username, EventMessageDTO eventMessageDTO) {
        SubscriptionData subscription = subscriptionService.findSubscription(roomId, username);
        EventType eventType = eventMessageDTO.getEventType();
        ServerSentEvent event = ServerSentEvent.builder()
                .data(eventMessageDTO.getMessage())
                .event(eventType.name())
                .build();
        subscription.getFluxSink().next(event);
        log.info("Message for event {} sent to subscription {}", eventType, username);
    }

    private EventMessageDTO<Set<ChatUserDTO>> createUsersRefreshedEvent(String roomId) {
        Set<ChatUser> chatUsers = chatUserService.findChatUsersByRoomId(roomId);
        return new EventMessageDTO<>(
                EventType.USERS_REFRESHED,
                toChatUserDTOs(chatUsers));
    }

    private Set<ChatUserDTO> toChatUserDTOs(Set<ChatUser> chatUsers) {
        return chatUsers.stream().map(chatUserMapper::chatUserToDTO).collect(Collectors.toSet());
    }

    private ChatUserDTO toChatUserDTO(ChatUser chatUser) {
        return chatUserMapper.chatUserToDTO(chatUser);
    }

}
