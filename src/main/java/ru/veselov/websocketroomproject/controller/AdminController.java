package ru.veselov.websocketroomproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.TagService;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Admin controller", description = "API for managing administrator actions")
public class AdminController {

    private final TagService tagService;

    private final RoomService roomService;

    @Operation(summary = "Delete requested tag",
            description = "Delete requested tag and return updated array with Tags",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully deleted",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tag.class)),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))},
            parameters = @Parameter(in = ParameterIn.PATH, description = "Tag name", required = true))
    @DeleteMapping("/delete/tag/{name}")
    public Set<Tag> deleteTag(@PathVariable("name") String name) {
        return tagService.deleteTag(name);
    }

    @Operation(summary = "Add tag", description = "Added new tag and return updated array of tags",
            responses = @ApiResponse(responseCode = "201", description = "Successfully created",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tag.class)))),
            parameters = @Parameter(in = ParameterIn.PATH, description = "New tag name", required = true))
    @PostMapping("/add/tag/{tag}")
    @ResponseStatus(HttpStatus.CREATED)
    public Set<Tag> addTag(@PathVariable("tag") String tag) {
        return tagService.addTag(tag);
    }

    @Operation(summary = "Delete room", description = "Deleting room",
            responses = @ApiResponse(responseCode = "200", description = "Successfully deleted",
                    content = @Content(schema = @Schema(implementation = String.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)),
            parameters = @Parameter(in = ParameterIn.PATH, description = "Room Id", required = true))
    @DeleteMapping("/delete/room/{roomId}")
    public String deleteRoom(@PathVariable("roomId") @UUID String roomId) {
        roomService.deleteRoom(roomId);
        return "Room %s deleted".formatted(roomId);
    }
}
