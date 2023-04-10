package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.Mapper;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.model.ChatUser;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ChatUserMapper {
    ChatUserDTO chatUserToDTO(ChatUser chatUser);

    Set<ChatUserDTO> chatUsersToDTO(Set<ChatUser> chatUserSet);

}
