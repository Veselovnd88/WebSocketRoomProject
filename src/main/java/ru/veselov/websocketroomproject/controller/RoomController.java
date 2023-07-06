package ru.veselov.websocketroomproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.annotation.SortingParam;
import ru.veselov.websocketroomproject.dto.request.SortParameters;
import ru.veselov.websocketroomproject.exception.error.ApiErrorResponse;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "room", description = "Room API")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Get room by Id (UUID)", description = "Return room with ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(
                            schema = @Schema(implementation = Room.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = {@Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )})
    })
    @GetMapping("/{roomId}")
    public Room getRoom(@Parameter(description = "Room ID as UUID", required = true,
            example = "1bd7c828-3a5c-4fd9-a2af-78b6a127459f")
                        @PathVariable("roomId") @UUID String id,
                        @Parameter(description = "Access token for private room")
                        @RequestParam(required = false, name = "token") String token) {
        return roomService.getRoomById(id, token);
    }

    @Operation(summary = "Create room", description = "Creates and returns Room")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room created",
                    content = @Content(
                            schema = @Schema(implementation = Room.class), mediaType = MediaType.APPLICATION_JSON_VALUE
                    )),
            @ApiResponse(responseCode = "409", description = "Room with such name already exists",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "error": "ERROR_CONFLICT",
                                      "code": 409
                                    }"""),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ))
    })
    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@io.swagger.v3.oas.annotations.parameters.RequestBody(content =
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
            {  "name": "newRoomName",
            "tags" : ["Movie","Other", "Anime"],
            "playerType" :"YOUTUBE"}""",
            description = "Room to create")))
                           @Valid @RequestBody Room room, Principal principal) {
        return roomService.createRoom(room, principal);
    }

    @Operation(summary = "Get all public rooms", description = "Returns array of Rooms")
    @Parameters({@Parameter(in = ParameterIn.QUERY, name = "page", example = "0"),
            @Parameter(in = ParameterIn.QUERY, name = "sort", example = "name",
                    description = "Sorting field, createdAt by default," +
                            " available: name, ownerName, changedAt, playerType, createdAt"),
            @Parameter(in = ParameterIn.QUERY, name = "order", example = "desc",
                    description = "Sorting order, desc by default, available: asc, desc")
    })
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = Room.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping("/all")
    public List<Room> getAllRooms(
            @Schema(accessMode = Schema.AccessMode.READ_ONLY, hidden = true)
            @Valid @SortingParam SortParameters parameters) {
        return roomService.findAll(parameters.getPage(), parameters.getSort(), parameters.getOrder());
    }

    @Operation(summary = "Get Room by Tag and sorting parameters",
            description = "Returns array of public Rooms selected by chosen tag")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "Tag", description = "Tag name for selecting rooms"),
            @Parameter(in = ParameterIn.QUERY, name = "page", example = "0"),
            @Parameter(in = ParameterIn.QUERY, name = "sort", example = "name",
                    description = "Sorting field, createdAt by default," +
                            " available: name, ownerName, changedAt, playerType, createdAt"),
            @Parameter(in = ParameterIn.QUERY, name = "order", example = "desc",
                    description = "Sorting order, desc by default, available: asc, desc")
    })
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = Room.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping("/all/{tag}")
    public List<Room> getRoomsByTag(@PathVariable String tag,
                                    @Schema(accessMode = Schema.AccessMode.READ_ONLY, hidden = true)
                                    @Valid @SortingParam SortParameters parameters) {
        return roomService.findAllByTag(tag, parameters.getPage(), parameters.getSort(), parameters.getOrder());
    }

}
