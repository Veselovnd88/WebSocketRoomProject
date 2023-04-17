package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.Mapper;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.model.ChatUser;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ChatUserEntityMapper {

    ChatUserEntity toChatUserEntity(ChatUser chatUser);

    ChatUser toChatUser(ChatUserEntity chatUser);

    Set<ChatUser> toChatUsersSet(Set<ChatUserEntity> chatUserEntities);

}
