package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoomController {

    private final StreamingService service;


    @GetMapping(value = "/room/{roomId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<Resource> getVideos(@PathVariable String roomId, @RequestHeader("Range") String range) {
        System.out.println("range in bytes() : " + range);
        try {
            return service.getVideo(roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
