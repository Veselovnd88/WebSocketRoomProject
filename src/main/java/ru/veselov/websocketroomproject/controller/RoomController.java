package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoomController {

    private final StreamingService service;


    @GetMapping(value = "/room/{roomId}", produces = "video/mp4")
    public Mono<Resource> getVideos(@PathVariable String roomId, @RequestHeader("Range") String range) {
        System.out.println("range in bytes() : " + range);
        return service.getVideo(roomId);
    }
}
