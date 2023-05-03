package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;

@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = "http://localhost:8080")
@Slf4j
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable("roomId") String id) {
        Room room = roomService.getRoomById(id);
        log.info("[Room {}] retrieved", id);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PatchMapping("/change-owner")
    public ResponseEntity<Room> changeOwner(@RequestBody Room room, Principal principal) {
        Room editedRoom = roomService.changeOwner(room, principal);
        log.info("[Room {}] owner changed to [{}]", room.getId(), room.getOwnerName());
        return new ResponseEntity<>(editedRoom, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/change-status")
    public ResponseEntity<Room> changeStatus(@RequestBody Room room, Principal principal) {
        Room editedRoom = roomService.changeStatus(room, principal);
        log.info("[Room {}] status changed to [{}]", room.getId(), room.getIsPrivate());
        return new ResponseEntity<>(editedRoom, HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Room> createRoom(@RequestBody Room room, Principal principal) {
        room.setOwnerName(principal.getName());
        Room saved = roomService.createRoom(room);
        log.info("[Room {}] created", saved.getName());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

}