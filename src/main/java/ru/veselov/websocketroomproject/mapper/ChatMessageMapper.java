package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;
import ru.veselov.websocketroomproject.dto.request.ReceivedChatMessage;
import ru.veselov.websocketroomproject.dto.response.SendChatMessage;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ChatMessageMapper {

    @Value("${server.zoneId}")
    private String serverZoneId;

    @Mapping(target = "sentTime", ignore = true)
    public abstract SendChatMessage toSendChatMessage(ReceivedChatMessage receivedChatMessage);

    @AfterMapping
    public void after(@MappingTarget SendChatMessage sendChatMessage) {
        sendChatMessage.setSentTime(ZonedDateTime.now(ZoneId.of(serverZoneId)));
    }

}