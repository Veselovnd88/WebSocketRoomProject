package ru.veselov.websocketroomproject.service.impl;

import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.websocketroomproject.ChatUserUtils;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.exception.ChatUserNotFoundException;
import ru.veselov.websocketroomproject.mapper.ChatUserEntityMapper;
import ru.veselov.websocketroomproject.mapper.ChatUserEntityMapperImpl;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.repository.ChatUserRedisRepository;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ChatUserServiceImplTest {

    private static final String ROOM_ID = "5";

    @Mock
    ChatUserRedisRepository repository;

    @InjectMocks
    ChatUserServiceImpl chatUserService;

    private final Faker faker = new Faker();

    @Captor
    ArgumentCaptor<ChatUserEntity> argumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(
                chatUserService,
                "chatUserEntityMapper",
                new ChatUserEntityMapperImpl(),
                ChatUserEntityMapper.class);
    }

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

        Optional<ChatUser> chatUserOptional = chatUserService.removeChatUser(TestConstants.TEST_SESSION_ID);

        ChatUser chatUser = chatUserOptional.get(); //FIXME
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