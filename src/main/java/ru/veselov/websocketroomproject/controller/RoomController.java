package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.dto.RoomCreationDTO;
import ru.veselov.websocketroomproject.dto.RoomInfoDTO;
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
    public ResponseEntity<Room> createRoom(@RequestBody RoomCreationDTO roomCreationDTO,
                                           Principal principal) {
        log.info("Create [room {}]", roomCreationDTO.getRoomName());
        Room room = roomService.createRoom(roomCreationDTO.getRoomName(),
                principal.getName(), roomCreationDTO.getIsPrivate());
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

}
