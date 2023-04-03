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
import ru.veselov.websocketroomproject.service.RoomSubscriptionService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class EventMessageServiceImpl implements EventMessageService {

    private final RoomSubscriptionService subscriptionService;

    private final ChatUserService chatUserService;

    private final ChatUserMapper chatUserMapper;

    @Override
    public void sendUserListToAllSubscriptions(String roomId) {
        Set<ChatUser> chatUsers = chatUserService.findChatUsersByRoomId(roomId);
        EventMessageDTO<Set<ChatUserDTO>> eventMsg = new EventMessageDTO<>(
                EventType.USERS_REFRESHED,
                toChatUserDTOs(chatUsers));
        sendEventMessageToAllSubscriptions(roomId, eventMsg);
    }

    @Override
    public void sendUserConnectedMessageToAll(ChatUser chatUser) {
        EventMessageDTO<ChatUserDTO> eventMessageDTO = new EventMessageDTO<>(
                EventType.CONNECTED,
                toChatUserDTO(chatUser));
        sendEventMessageToAllSubscriptions(chatUser.getRoomId(), eventMessageDTO);
    }

    @Override
    public void sendUserDisconnectedMessageToAll(ChatUser chatUser) {
        EventMessageDTO<ChatUserDTO> eventMessageDTO = new EventMessageDTO<>(
                EventType.DISCONNECTED,
                toChatUserDTO(chatUser));
        sendEventMessageToAllSubscriptions(chatUser.getRoomId(), eventMessageDTO);
    }

    private void sendEventMessageToAllSubscriptions(String roomId, EventMessageDTO eventMessageDTO) {
        Set<SubscriptionData> subscriptionsByRoomId = subscriptionService.findSubscriptionsByRoomId(roomId);
        EventType eventType = eventMessageDTO.getEventType();
        ServerSentEvent event = ServerSentEvent.builder()
                .data(eventMessageDTO)
                .event(eventType.name())
                .build();
        subscriptionsByRoomId.forEach(x -> x.getFluxSink().next(event));
        log.info("Message for event {} sent to all connected subscriptions of room #{}", eventType, roomId);
    }

    private Set<ChatUserDTO> toChatUserDTOs(Set<ChatUser> chatUsers) {
        return chatUsers.stream().map(chatUserMapper::chatUserToDTO).collect(Collectors.toSet());
    }

    private ChatUserDTO toChatUserDTO(ChatUser chatUser) {
        return chatUserMapper.chatUserToDTO(chatUser);
    }

}