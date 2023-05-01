package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.dto.RoomCreationDTO;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = "http://localhost:8080")
@Slf4j
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getInfo(@PathVariable("roomId") UUID uuid) {
        log.info("Get [room {}] info", uuid);
        Room room = roomService.getRoom(uuid);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Room> createRoom(@RequestBody Room room,
                                           Principal principal) {
        log.info("Create [room {}]", room.getName());
        room.setOwnerName(principal.getName());
        log.warn("[{}]", room);
        Room saved = roomService.createRoom(room);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

}
