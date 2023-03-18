package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.Mapper;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.model.ChatUser;

@Mapper(componentModel = "spring")
public interface ChatUserMapper {
    ChatUserDTO chatUserToDTO(ChatUser chatUser);

    ChatUser dtoToChatUser(ChatUserDTO chatUserDTO);
}
