package ru.veselov.websocketroomproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import ru.veselov.websocketroomproject.controller.EventType;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.EventMessageDTO;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventMessageService {

    private final SubscriptionService subscriptionService;

    private final ChatUserService chatUserService;
    private final ChatUserMapper chatUserMapper;

    public void sendEventMessageToSubscriptions(String roomId, EventMessageDTO eventMessageDTO) {
        List<FluxSink<ServerSentEvent>> subscriptionsByRoomId = subscriptionService.findSubscriptionsByRoomId(roomId);
        EventType eventType = eventMessageDTO.getEventType();
        ServerSentEvent event = ServerSentEvent.builder()
                .data(eventMessageDTO)
                .event(eventType.name())
                .build();

        subscriptionsByRoomId.forEach(x -> x.next(event));

        log.info("Message for event {} sent to all emitters of room #{}", eventType, roomId);
    }


    public void sendUserList(String roomId) {
        Set<ChatUser> chatUsers = chatUserService.findChatUsersByRoomId(roomId);
        EventMessageDTO<Set<ChatUserDTO>> listMessage = new EventMessageDTO<>(
                EventType.USERS_REFRESHED,
                toChatUserDTOs(chatUsers));
        sendEventMessageToSubscriptions(roomId, listMessage);
    }

    public void sendUserConnectedMessage(ChatUser chatUser) {
        EventMessageDTO<ChatUserDTO> eventMessageDTO = new EventMessageDTO<>(
                EventType.CONNECTED,
                toChatUserDTO(chatUser));
        sendEventMessageToSubscriptions(chatUser.getRoomId(), eventMessageDTO);
    }

    private Set<ChatUserDTO> toChatUserDTOs(Set<ChatUser> chatUsers) {
        return chatUsers.stream().map(chatUserMapper::chatUserToDTO).collect(Collectors.toSet());
    }

    private ChatUserDTO toChatUserDTO(ChatUser chatUser) {
        return chatUserMapper.chatUserToDTO(chatUser);
    }


}
