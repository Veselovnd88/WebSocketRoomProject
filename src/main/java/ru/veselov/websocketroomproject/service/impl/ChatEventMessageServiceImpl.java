package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.dto.response.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.dto.response.SseDataDto;
import ru.veselov.websocketroomproject.event.sender.RoomSubscriptionEventSender;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.ChatEventMessageService;

import java.util.Set;

/**
 * Sends chat events to several groups of subscriptions
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatEventMessageServiceImpl implements ChatEventMessageService {

    private final ChatUserService chatUserService;

    private final ChatUserMapper chatUserMapper;

    private final RoomSubscriptionEventSender roomSubscriptionEventSender;

    @Override
    public void sendUserListToAllSubscriptions(String roomId) {
        Set<ChatUser> chatUsers = chatUserService.findChatUsersByRoomId(roomId);
        EventMessageDTO<Set<ChatUserDTO>> eventMessageDTO = new EventMessageDTO<>(
                EventType.USER_LIST_REFRESH, new SseDataDto<>("New List of users", toChatUserDTOs(chatUsers)));
        roomSubscriptionEventSender.sendEventToRoomSubscriptions(roomId, eventMessageDTO);
    }

    @Override
    public void sendUserConnectedMessageToAll(ChatUser chatUser) {
        EventMessageDTO<ChatUserDTO> eventMessageDTO = new EventMessageDTO<>(
                EventType.USER_CONNECT, new SseDataDto<>("Connected User", toChatUserDTO(chatUser)));
        roomSubscriptionEventSender.sendEventToRoomSubscriptions(chatUser.getRoomId(), eventMessageDTO);
    }

    @Override
    public void sendUserDisconnectedMessageToAll(ChatUser chatUser) {
        EventMessageDTO<ChatUserDTO> eventMessageDTO = new EventMessageDTO<>(
                EventType.USER_DISCONNECT, new SseDataDto<>("Disconnected user", toChatUserDTO(chatUser)));
        roomSubscriptionEventSender.sendEventToRoomSubscriptions(chatUser.getRoomId(), eventMessageDTO);
    }

    private Set<ChatUserDTO> toChatUserDTOs(Set<ChatUser> chatUsers) {
        return chatUserMapper.chatUsersToDTO(chatUsers);
    }

    private ChatUserDTO toChatUserDTO(ChatUser chatUser) {
        return chatUserMapper.chatUserToDTO(chatUser);
    }

}