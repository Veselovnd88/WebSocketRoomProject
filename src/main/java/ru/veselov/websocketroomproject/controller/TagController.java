package ru.veselov.websocketroomproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.service.TagService;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/room/tag")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag controller", description = "API for managing tags")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Get all tags", description = "Returns array of Tags")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Room.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE)
    )
    @GetMapping("/all")
    public Set<Tag> getAllTags() {
        return tagService.getTags();
    }

}
