package ru.veselov.websocketroomproject.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.veselov.websocketroomproject.annotations.SupportedPlayer;
import ru.veselov.websocketroomproject.entity.PlayerType;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room implements Serializable {

    private UUID id;

    @NotEmpty(message = "Room name cannot be empty")
    @Size(min = 3, max = 30, message = "Name length should be from 3 to 30 symbols")
    private String name;

    @NotNull(message = "Room status cannot be null")
    private Boolean isPrivate;

    private String activeUrl;

    private String roomToken;

    private String ownerName;
    @SupportedPlayer
    private PlayerType playerType;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private ZonedDateTime createdAt;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private ZonedDateTime changedAt;

}
