package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class YouTubeVideoController {

    private final StreamingService service;


    @GetMapping(value = "/room/{roomId}")
    public void getVideos(@PathVariable String roomId, @RequestParam(value = "scroll", required = false) String scroll) {
        log.info("Paused");
        log.info("scrolled to: {}", scroll);

    }
}
