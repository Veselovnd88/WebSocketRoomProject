package ru.veselov.websocketroomproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.exception.error.ApiErrorResponse;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomSettingsService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/room")
@Validated
@RequiredArgsConstructor
@Tag(name = "Room settings controller", description = "API for managing room settings")
@ApiResponse(responseCode = "404", description = "Room not found",
        content = {@Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE
        )})
@ApiResponse(responseCode = "403", description = "Authorization failed",
        content = @Content(
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = """
                            {
                              "error": "ERROR_NOT_ROOM_OWNER",
                              "code": 403,
                              "message": "You are not owner of room"
                            }"""),
                mediaType = MediaType.APPLICATION_JSON_VALUE
        ))
public class RoomSettingController {

    private final RoomSettingsService roomSettingsService;

    @Operation(summary = "Apply new settings for room",
            description = "Returns updated Room, and notify all subscriptions")
    @ApiResponse(responseCode = "202", description = "Success",
            content = @Content(schema = @Schema(implementation = Room.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Room changeSettings(@Parameter(in = ParameterIn.PATH, description = "Room ID as UUID", required = true,
            example = "1bd7c828-3a5c-4fd9-a2af-78b6a127459f")
                               @PathVariable("roomId") @UUID String roomId,
                               @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
                               @Content(schema = @Schema(implementation = RoomSettingsDTO.class),
                                       mediaType = MediaType.APPLICATION_JSON_VALUE))
                               @Valid @RequestBody RoomSettingsDTO settings,
                               Principal principal) {
        return roomSettingsService.changeSettings(roomId, settings, principal);
    }

    @Operation(summary = "Add new active URL to the room", description = "Return URL and refresh all subscriptions")
    @ApiResponse(responseCode = "202", description = "Success",
            content = @Content(schema = @Schema(implementation = UrlDto.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    @PostMapping(value = "/url/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UrlDto processUrl(@Parameter(in = ParameterIn.PATH, description = "Room ID as UUID", required = true,
            example = "1bd7c828-3a5c-4fd9-a2af-78b6a127459f")
                             @PathVariable("roomId") @UUID String roomId,
                             @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
                             @Content(schema = @Schema(implementation = UrlDto.class),
                                     mediaType = MediaType.APPLICATION_JSON_VALUE))
                             @Valid @RequestBody UrlDto urlDto,
                             Principal principal) {
        roomSettingsService.addUrl(roomId, urlDto.getUrl(), principal);
        return urlDto;
    }

}
