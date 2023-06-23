package ru.veselov.websocketroomproject.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.websocketroomproject.TestConstants;
import ru.veselov.websocketroomproject.entity.ChatUserEntity;
import ru.veselov.websocketroomproject.ChatUserUtils;

import java.util.Optional;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ChatUserRedisRepositoryIntegrationTest extends RedisTestContainersConfiguration {

    private final static String ROOM_ID = "5";

    @Autowired
    ChatUserRedisRepository chatUserRedisRepository;

    @Test
    void shouldSaveRetrieveAndDeleteCorrectWithOneRoom() {
        for (int i = 0; i < 10; i++) {
            ChatUserEntity chatUser = ChatUserUtils
                    .getChatUser(ROOM_ID, TestConstants.TEST_USERNAME + i, TestConstants.TEST_SESSION_ID + i);

            chatUserRedisRepository.save(chatUser);
        }

        Optional<ChatUserEntity> chatUserEntity = chatUserRedisRepository.findById(TestConstants.TEST_SESSION_ID + 0);
        Assertions.assertThat(chatUserEntity).isPresent();
        ChatUserEntity fromRedis = chatUserEntity.get();
        Assertions.assertThat(fromRedis.getSession()).isEqualTo(TestConstants.TEST_SESSION_ID + 0);

        Set<ChatUserEntity> allByRoomId = chatUserRedisRepository.findAllByRoomId(ROOM_ID);
        Assertions.assertThat(allByRoomId).hasSize(10);

        chatUserRedisRepository.deleteById(TestConstants.TEST_SESSION_ID + 0);

        Set<ChatUserEntity> afterDeleting = chatUserRedisRepository.findAllByRoomId(ROOM_ID);
        Assertions.assertThat(afterDeleting).hasSize(9);
    }

    @Test
    void shouldSaveRetrieveAndDeleteCorrectWithDifferentRooms() {
        for (int i = 0; i < 10; i++) {
            ChatUserEntity chatUser = ChatUserUtils
                    .getChatUser(ROOM_ID + i, TestConstants.TEST_USERNAME + i, TestConstants.TEST_SESSION_ID + i);

            chatUserRedisRepository.save(chatUser);
        }

        Optional<ChatUserEntity> chatUserEntity = chatUserRedisRepository.findById(TestConstants.TEST_SESSION_ID + 0);
        Assertions.assertThat(chatUserEntity).isPresent();
        ChatUserEntity fromRedis = chatUserEntity.get();
        Assertions.assertThat(fromRedis.getSession()).isEqualTo(TestConstants.TEST_SESSION_ID + 0);

        Set<ChatUserEntity> allByRoomId = chatUserRedisRepository.findAllByRoomId("50");
        Assertions.assertThat(allByRoomId).hasSize(1);

        chatUserRedisRepository.deleteById(TestConstants.TEST_SESSION_ID + 0);

        Set<ChatUserEntity> afterDeleting = chatUserRedisRepository.findAllByRoomId("50");
        Assertions.assertThat(afterDeleting).isEmpty();
    }

}