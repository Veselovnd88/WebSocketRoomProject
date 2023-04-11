package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.*;
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;

import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ChatMessageMapper {

    public abstract SendChatMessage toSendChatMessage(ReceivedChatMessage receivedChatMessage);

    @AfterMapping
    public void after(@MappingTarget SendChatMessage sendChatMessage) {
        sendChatMessage.setSentTime(ZonedDateTime.now());
    }

}
