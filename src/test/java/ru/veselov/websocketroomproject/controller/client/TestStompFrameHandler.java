package ru.veselov.websocketroomproject.controller.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import ru.veselov.websocketroomproject.dto.SendChatMessage;

import java.lang.reflect.Type;
import java.util.function.Consumer;

@Slf4j
public class TestStompFrameHandler implements StompFrameHandler {

    private final Consumer<SendChatMessage> frameHandler;

    public TestStompFrameHandler(Consumer<SendChatMessage> frameHandler) {
        this.frameHandler = frameHandler;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return SendChatMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.info("received message: {} with headers: {}", payload, headers);
        frameHandler.accept((SendChatMessage) payload);
    }

}
