package ru.veselov.websocketroomproject.service.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.veselov.websocketroomproject.ChatUserUtils;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.exception.ChatUserNotFoundException;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.repository.ChatUserRedisRepository;
import ru.veselov.websocketroomproject.service.ChatUserService;

import java.util.Optional;
import java.util.Set;

@SpringBootTest
class ChatUserServiceImplTest {

    private static final String ROOM_ID = "5";

    @MockBean
    ChatUserRedisRepository repository;

    @Autowired
    ChatUserService chatUserService;

    private final Faker faker = new Faker();

    @Captor
    ArgumentCaptor<ChatUserEntity> argumentCaptor;

    @Test
    void shouldSaveChatUserToRepo() {
        ChatUser chatUser = generateUser();

        chatUserService.saveChatUser(chatUser);

        Mockito.verify(repository, Mockito.times(1)).save(argumentCaptor.capture());
        ChatUserEntity captured = argumentCaptor.getValue();
        Assertions.assertThat(captured.getUsername()).isEqualTo(chatUser.getUsername());
        Assertions.assertThat(captured.getSession()).isEqualTo(chatUser.getSession());
        Assertions.assertThat(captured.getRoomId()).isEqualTo(chatUser.getRoomId());
    }

    @Test
    void shouldFindChatUserBySessionIdFromRepo() {
        ChatUserEntity chatUserEntity = ChatUserUtils
                .getChatUser(ROOM_ID, TestConstants.TEST_USERNAME, TestConstants.TEST_SESSION_ID);
        Mockito.when(repository.findById(TestConstants.TEST_SESSION_ID)).thenReturn(Optional.of(chatUserEntity));

        ChatUser userBySessionId = chatUserService.findChatUserBySessionId(TestConstants.TEST_SESSION_ID);

        Mockito.verify(repository, Mockito.times(1)).findById(TestConstants.TEST_SESSION_ID);
        Assertions.assertThat(userBySessionId.getUsername()).isEqualTo(chatUserEntity.getUsername());
        Assertions.assertThat(userBySessionId.getSession()).isEqualTo(chatUserEntity.getSession());
        Assertions.assertThat(userBySessionId.getRoomId()).isEqualTo(chatUserEntity.getRoomId());
    }

    @Test
    void shouldThrowExceptionIfChatUserNotFound() {
        Mockito.when(repository.findById(TestConstants.TEST_SESSION_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> chatUserService.findChatUserBySessionId(TestConstants.TEST_SESSION_ID))
                .isInstanceOf(ChatUserNotFoundException.class);

        Mockito.verify(repository, Mockito.times(1)).findById(TestConstants.TEST_SESSION_ID);
    }

    @Test
    void shouldReturnChatUsersOfRoom() {
        ChatUserEntity chatUserEntity = ChatUserUtils
                .getChatUser(ROOM_ID, TestConstants.TEST_USERNAME, TestConstants.TEST_SESSION_ID);
        ChatUserEntity chatUserEntity2 = ChatUserUtils
                .getChatUser(ROOM_ID, TestConstants.TEST_USERNAME + 2, TestConstants.TEST_SESSION_ID + 2);
        Set<ChatUserEntity> chatUserEntitySet = Set.of(
                chatUserEntity,
                chatUserEntity2
        );
        Mockito.when(repository.findAllByRoomId(ROOM_ID)).thenReturn(chatUserEntitySet);

        Set<ChatUser> chatUsersByRoomId = chatUserService.findChatUsersByRoomId(ROOM_ID);

        Assertions.assertThat(chatUsersByRoomId).isNotEmpty().hasSize(2)
                .contains(
                        new ChatUser(TestConstants.TEST_USERNAME, ROOM_ID, TestConstants.TEST_SESSION_ID),
                        new ChatUser(TestConstants.TEST_USERNAME + 2, ROOM_ID, TestConstants.TEST_SESSION_ID + 2)
                );
    }

    @Test
    void shouldRemoveChatUser() {
        ChatUserEntity chatUserEntity = ChatUserUtils
                .getChatUser(ROOM_ID, TestConstants.TEST_USERNAME, TestConstants.TEST_SESSION_ID);
        Mockito.when(repository.findById(TestConstants.TEST_SESSION_ID)).thenReturn(Optional.of(chatUserEntity));

        ChatUser chatUser = chatUserService.removeChatUser(TestConstants.TEST_SESSION_ID);

        Mockito.verify(repository, Mockito.times(1)).findById(TestConstants.TEST_SESSION_ID);
        Mockito.verify(repository, Mockito.times(1)).delete(chatUserEntity);
        Assertions.assertThat(chatUser.getUsername()).isEqualTo(chatUserEntity.getUsername());
        Assertions.assertThat(chatUser.getSession()).isEqualTo(chatUserEntity.getSession());
        Assertions.assertThat(chatUser.getRoomId()).isEqualTo(chatUserEntity.getRoomId());
    }

    @Test
    void shouldThrowExceptionIfNoUserInRepository() {
        Mockito.when(repository.findById(TestConstants.TEST_SESSION_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> chatUserService.removeChatUser(TestConstants.TEST_SESSION_ID))
                .isInstanceOf(ChatUserNotFoundException.class);

        Mockito.verify(repository, Mockito.times(1)).findById(TestConstants.TEST_SESSION_ID);
        Mockito.verify(repository, Mockito.never()).delete(ArgumentMatchers.any(ChatUserEntity.class));
    }


    private ChatUser generateUser() {
        return new ChatUser(faker.name().username(), ROOM_ID, faker.expression("#{letterify '???????'}"));
    }

}