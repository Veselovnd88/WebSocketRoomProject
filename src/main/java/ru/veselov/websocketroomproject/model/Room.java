package ru.veselov.websocketroomproject.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.veselov.websocketroomproject.entity.PlayerType;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room implements Serializable {
    @Schema(description = "UUID identifier of Room", example = "1bd7c828-3a5c-4fd9-a2af-78b6a127459f")
    private UUID id;

    @Schema(description = "Name of new Room", example = "MyProgrammingRoom")
    @NotEmpty(message = "Room name cannot be empty")
    @Size(min = 3, max = 30, message = "Name length should be from 3 to 30 symbols")
    private String name;

    @Schema(description = "Status of room")
    private Boolean isPrivate;

    @Schema(description = "Active Url", example = "https://youtube.com")
    private String activeUrl;

    @Schema(description = "Token for Private room", example = "asdfasdf")
    private String roomToken;

    @Schema(description = "Name of Room owner", example = "Vasya")
    private String ownerName;

    @Schema(description = "Player Type", example = "YOUTUBE")
    @NotNull(message = "Player type cannot be null")
    private PlayerType playerType;

    @Schema(description = "Array with tags")
    @NotEmpty(message = "Room should has at least 1 tag")
    @NotNull(message = "Room should has at least 1 tag")
    private Set<Tag> tags;

    @Schema(description = "Created time")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private ZonedDateTime createdAt;

    @Schema(description = "Last update time")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private ZonedDateTime changedAt;

}
