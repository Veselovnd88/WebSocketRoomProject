package ru.veselov.websocketroomproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.dto.RoomInfoDTO;

import java.security.Principal;

@RestController
@RequestMapping("/api/room/getInfo")
@Slf4j
public class OwnerTestController {


    @GetMapping("/{roomId}")
    public RoomInfoDTO getOwner(@PathVariable("roomId") String roomId, Principal principal) {
        log.info("Getting room information");
        RoomInfoDTO roomInfoDTO = new RoomInfoDTO();
        roomInfoDTO.setOwner("user1");
        roomInfoDTO.setRoomId(roomId);
        roomInfoDTO.setUsername(principal.getName());
        return roomInfoDTO;
    }
}
