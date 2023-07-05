package ru.veselov.websocketroomproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.service.TagService;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/room/tag")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class TagController {

    private final TagService tagService;

    @GetMapping("/all")
    public Set<Tag> getAllTags() {
        return tagService.getTags();
    }

}
