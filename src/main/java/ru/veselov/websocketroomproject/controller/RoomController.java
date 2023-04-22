package ru.veselov.websocketroomproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.dto.RoomInfoDTO;

import java.security.Principal;

@RestController
@RequestMapping("/api/room")
@Slf4j
public class RoomController {

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomInfoDTO> getInfo(@PathVariable("roomId") String roomId) {
        log.info("Get [room {}] info", roomId);
        RoomInfoDTO roomInfoDTO = new RoomInfoDTO("user1", roomId, "vasya", "token", "myRoom");
        return new ResponseEntity<>(roomInfoDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<RoomInfoDTO> createRoom(@RequestBody String roomName) {
        log.info("Create [room with name {}]", roomName);
        //roomService.createRoom
        RoomInfoDTO roomInfoDTO = new RoomInfoDTO("user1", "5", "user", "token", "myRoom");
        return new ResponseEntity<>(roomInfoDTO, HttpStatus.CREATED);
    }

}
