package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.dto.response.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.response.EventMessageDTO;
import ru.veselov.websocketroomproject.event.EventSender;
import ru.veselov.websocketroomproject.event.EventType;
import ru.veselov.websocketroomproject.mapper.ChatUserMapper;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EventMessageService;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventMessageServiceImpl implements EventMessageService {

    private final ChatUserService chatUserService;

    private final ChatUserMapper chatUserMapper;

    private final EventSender eventSender;

    @Override
    public void sendUserListToAllSubscriptions(String roomId) {
        Set<ChatUser> chatUsers = chatUserService.findChatUsersByRoomId(roomId);
        EventMessageDTO<Set<ChatUserDTO>> eventMessageDTO = new EventMessageDTO<>(
                EventType.USER_LIST_REFRESH,
                toChatUserDTOs(chatUsers));
        eventSender.sendEventToRoomSubscriptions(roomId, eventMessageDTO);
    }

    @Override
    public void sendUserConnectedMessageToAll(ChatUser chatUser) {
        EventMessageDTO<ChatUserDTO> eventMessageDTO = new EventMessageDTO<>(
                EventType.USER_CONNECT,
                toChatUserDTO(chatUser));
        eventSender.sendEventToRoomSubscriptions(chatUser.getRoomId(), eventMessageDTO);
    }

    @Override
    public void sendUserDisconnectedMessageToAll(ChatUser chatUser) {
        EventMessageDTO<ChatUserDTO> eventMessageDTO = new EventMessageDTO<>(
                EventType.USER_DISCONNECT,
                toChatUserDTO(chatUser));
        eventSender.sendEventToRoomSubscriptions(chatUser.getRoomId(), eventMessageDTO);
    }

    private Set<ChatUserDTO> toChatUserDTOs(Set<ChatUser> chatUsers) {
        return chatUserMapper.chatUsersToDTO(chatUsers);
    }

    private ChatUserDTO toChatUserDTO(ChatUser chatUser) {
        return chatUserMapper.chatUserToDTO(chatUser);
    }

}