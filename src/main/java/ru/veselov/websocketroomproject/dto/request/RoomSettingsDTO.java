package ru.veselov.websocketroomproject.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.model.Tag;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettingsDTO {

    @Schema(description = "Room name", example = "MyAwesomeRooom")
    @Size(min = 3, max = 30, message = "Name length should be from 3 to 30 symbols")
    private String roomName;

    @Schema(description = "Name of owner", example = "Owner")
    private String ownerName;

    @Schema(description = "Status of room")
    private Boolean isPrivate;

    @Schema(description = "PLayer type", example = "YOUTUBE")
    private PlayerType playerType;

    @Schema(description = "Should i change token?")
    private Boolean changeToken;

    @ArraySchema(schema = @Schema(description = "Tags", example = "Java"), minItems = 1, uniqueItems = true)
    private Set<Tag> tags;

}
