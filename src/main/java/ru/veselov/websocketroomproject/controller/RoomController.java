package ru.veselov.websocketroomproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import ru.veselov.websocketroomproject.exception.error.ValidationErrorResponse;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Get room by Id (UUID)", description = "Retrieve Room by id from database")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = {@Content(
                            schema = @Schema(implementation = Room.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404",
                    content = {@Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(
                            schema = @Schema(implementation = ValidationErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )})
    })
    @GetMapping("/{roomId}")
    public Room getRoom(
            @Parameter(description = "Room Id as UUID", required = true,
                    example = "1bd7c828-3a5c-4fd9-a2af-78b6a127459f")
            @PathVariable("roomId") @UUID String id,
            @Parameter(description = "Access token for private room")
            @RequestParam(required = false, name = "token") String token) {
        return roomService.getRoomById(id, token);
    }

    @Operation(summary = "Create room", description = "Create room and save to database")
    @Parameters({
            @Parameter(in = ParameterIn.DEFAULT, name = "Room object",
                    content = @Content(
                            schema = @Schema(implementation = Room.class), mediaType = MediaType.APPLICATION_JSON_VALUE
                    ))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(
                            schema = @Schema(implementation = Room.class), mediaType = MediaType.APPLICATION_JSON_VALUE
                    )),
            @ApiResponse(responseCode = "409",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE
                    )),
            @ApiResponse(responseCode = "400",
                    content = {@Content(
                            schema = @Schema(implementation = ValidationErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )})
    })
    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@Valid @RequestBody
                           @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
                           @Content(examples = {
                                   @ExampleObject(
                                           name = "Room sample",
                                           summary = "Room dto example",
                                           value = """
                                                   { "name": "nameFrom3To30Symbols",
                                                               "isPrivate": false,
                                                               "tags": ["Movie","Other","Cartoon", "Buba"],
                                                               "playerType": "YOUTUBE" }
                                                   """
                                   )
                           }))
                           Room room, Principal principal) {
        return roomService.createRoom(room, principal);
    }

    @Operation(summary = "Get all public rooms", description = "Retrieve rooms from database")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "page", example = "0"),
            @Parameter(in = ParameterIn.PATH, name = "sort", example = "name",
                    description = "If not specified: createdAt, available: name, createdAt, ownerName, changedAt, playerType"),
            @Parameter(in = ParameterIn.PATH, name = "order", example = "asc",
                    description = "If not specified: desc, available: asc, desc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Room.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )),
            @ApiResponse(responseCode = "400",
                    content = @Content(
                            schema = @Schema(implementation = ValidationErrorResponse.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ))
    })
    @GetMapping("/all")
    public List<Room> getAllRooms(@Valid @SortingParam SortParameters parameters) {
        return roomService.findAll(parameters.getPage(), parameters.getSort(), parameters.getOrder());
    }

    @Operation(summary = "Get Room by Tag and sorting parameters", description = "Get rooms from database")
    @GetMapping("/all/{tag}")
    public List<Room> getRoomsByTag(@PathVariable String tag, @Valid @SortingParam SortParameters parameters) {
        return roomService.findAllByTag(tag, parameters.getPage(), parameters.getSort(), parameters.getOrder());
    }

}
