package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.*;
import ru.veselov.websocketroomproject.dto.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.SendChatMessage;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ChatMessageMapper {
    @Mapping(target = "sentTime", ignore = true)
    public abstract SendChatMessage toSendChatMessage(ReceivedChatMessage receivedChatMessage);

    @AfterMapping
    public void after(@MappingTarget SendChatMessage sendChatMessage, ReceivedChatMessage receivedChatMessage) {
        ZoneId zoneId = ZoneId.of(receivedChatMessage.getZoneId());
        sendChatMessage.setSentTime(ZonedDateTime.now(zoneId));
    }

}