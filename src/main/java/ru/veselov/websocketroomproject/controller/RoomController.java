package ru.veselov.websocketroomproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.annotation.SortingParam;
import ru.veselov.websocketroomproject.config.openapi.OpenApiExampleConstants;
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
@Tag(name = "Room controller", description = "API for managing room")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Get room by Id (UUID)", description = "Returns room by requested Id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = {@Content(
                                    schema = @Schema(implementation = Room.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)}),
                    @ApiResponse(responseCode = "404", description = "Room with this Id doesnt exists",
                            content = {@Content(
                                    schema = @Schema(implementation = ApiErrorResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )}),
                    @ApiResponse(responseCode = "403", description = "Authorization failed",
                            content = @Content(
                                    schema = @Schema(implementation = ApiErrorResponse.class),
                                    examples = @ExampleObject(value = OpenApiExampleConstants.ERROR_NOT_ROOM_OWNER_MESSAGE),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ))},
            parameters = {
                    @Parameter(in = ParameterIn.PATH, description = "Room ID as UUID", required = true,
                            example = OpenApiExampleConstants.ROOM_UUID),
                    @Parameter(description = "Access token for private room")
            })
    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable("roomId") @UUID String id,
                        @RequestParam(required = false, name = "token") String token) {
        return roomService.getRoomById(id, token);
    }

    @Operation(summary = "Create room", description = "Creates and returns Room",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Room created",
                            content = @Content(
                                    schema = @Schema(implementation = Room.class), mediaType = MediaType.APPLICATION_JSON_VALUE
                            )),
                    @ApiResponse(responseCode = "409", description = "Room with such name already exists",
                            content = @Content(
                                    schema = @Schema(implementation = ApiErrorResponse.class),
                                    examples = @ExampleObject(value = OpenApiExampleConstants.ERROR_CONFLICT_MESSAGE),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            ))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = Room.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            value = OpenApiExampleConstants.CREATED_ROOM, description = "Room to create")))
    )
    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@Valid @RequestBody Room room, Principal principal) {
        return roomService.createRoom(room, principal);
    }

    @Operation(summary = "Get all public rooms", description = "Returns array of Rooms",
            responses = @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = Room.class)),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)),
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = OpenApiExampleConstants.PAGE, example = "0"),
                    @Parameter(in = ParameterIn.QUERY, name = OpenApiExampleConstants.SORT, example = "name",
                            description = OpenApiExampleConstants.SORT_FIELD_DESCRIPTION),
                    @Parameter(in = ParameterIn.QUERY, name = OpenApiExampleConstants.ORDER, example = "desc",
                            description = OpenApiExampleConstants.SORT_ORDER_DESCRIPTION)
            }
    )
    @GetMapping("/all")
    public List<Room> getAllRooms(@Schema(hidden = true) @Valid @SortingParam SortParameters parameters) {
        return roomService.findAll(parameters.getPage(), parameters.getSort(), parameters.getOrder());
    }

    @Operation(summary = "Get Room by Tag and sorting parameters",
            description = "Returns array of public Rooms selected by chosen tag",
            responses = @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = Room.class)),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "Tag", description = "Tag name for selecting rooms"),
                    @Parameter(in = ParameterIn.QUERY, name = OpenApiExampleConstants.PAGE, example = "0"),
                    @Parameter(in = ParameterIn.QUERY, name = OpenApiExampleConstants.SORT, example = "name",
                            description = OpenApiExampleConstants.SORT_FIELD_DESCRIPTION),
                    @Parameter(in = ParameterIn.QUERY, name = OpenApiExampleConstants.ORDER, example = "desc",
                            description = OpenApiExampleConstants.SORT_ORDER_DESCRIPTION)
            }
    )
    @GetMapping("/all/{tag}")
    public List<Room> getRoomsByTag(@PathVariable String tag,
                                    @Schema(hidden = true) @Valid @SortingParam SortParameters parameters) {
        return roomService.findAllByTag(tag, parameters.getPage(), parameters.getSort(), parameters.getOrder());
    }

}
