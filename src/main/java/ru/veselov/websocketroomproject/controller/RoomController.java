package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.dto.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = "http://localhost:8080")
@Slf4j
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable("roomId") String id,
                                        @RequestParam(required = false, name = "token") String token) {
        Room room = roomService.getRoomById(id, token);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<Room> changeSettings(@PathVariable("roomId") String roomId,
                                               @RequestBody RoomSettingsDTO settings, Principal principal) {
        Room editedRoom = roomService.changeSettings(roomId, settings, principal);
        return new ResponseEntity<>(editedRoom, HttpStatus.ACCEPTED);
    }
    
    @PostMapping(value = "/create")
    public ResponseEntity<Room> createRoom(@RequestBody Room room, Principal principal) {
        room.setOwnerName(principal.getName());
        Room saved = roomService.createRoom(room);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{roomId}")
    public ResponseEntity<UrlDto> processUrl(@PathVariable("roomId") String roomId,
                                             @RequestBody UrlDto urlDto, Principal principal) {
        roomService.addUrl(roomId, urlDto.getUrl(), principal);
        return new ResponseEntity<>(urlDto, HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return new ResponseEntity<>(roomService.getAllPublicRooms(), HttpStatus.OK);
    }

}