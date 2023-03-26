package ru.veselov.websocketroomproject.controller;

import lombok.SneakyThrows;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.service.ChatUserService;
import ru.veselov.websocketroomproject.service.EmitterService;
import ru.veselov.websocketroomproject.service.EventMessageService;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@WithMockUser(username = "user1")
@WebMvcTest(ServerEventController.class)
class ServerEventControllerTest {

    private final static String ROOM_ID = "5";
    private final Faker faker = new Faker();
    @Autowired
    public MockMvc mockMvc;

    @MockBean
    private EmitterService emitterService;
    @MockBean
    private EventMessageService eventMessageService;
    @MockBean
    private ChatUserService chatUserService;

    @Test
    @SneakyThrows
    void test() {
        Mockito.when(chatUserService.findChatUsersByRoomId(ArgumentMatchers.anyString()))
                .thenReturn(new HashSet<>(
                        faker.collection(this::generateUser).maxLen(4).generate())
                );
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/room/sse?roomId="+ROOM_ID))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();


    }

    private ChatUser generateUser() {
        return new ChatUser(faker.name().username(), ROOM_ID, faker.expression("#{letterify '???????'}"));
    }

}