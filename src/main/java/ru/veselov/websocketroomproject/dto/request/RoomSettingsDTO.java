package ru.veselov.websocketroomproject.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import ru.veselov.websocketroomproject.entity.PlayerType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettingsDTO {

    @NotEmpty
    @UUID(message = "Room id should be UUID")
    private String id;

    @Size(min = 3, max = 30, message = "Name length should be from 3 to 30 symbols")
    private String roomName;

    private String ownerName;

    private Boolean isPrivate;

    private PlayerType playerType;

    private Boolean changeToken;

}
