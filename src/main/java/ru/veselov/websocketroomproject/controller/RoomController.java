package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/room")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable("roomId") String id,
                        @RequestParam(required = false, name = "token") String token) {
        return roomService.getRoomById(id, token);
    }

    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Room changeSettings(@PathVariable("roomId") String roomId,
                               @RequestBody RoomSettingsDTO settings, Principal principal) {
        return roomService.changeSettings(roomId, settings, principal);
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@RequestBody Room room, Principal principal) {
        return roomService.createRoom(room, principal);
    }

    @PostMapping(value = "/url/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UrlDto processUrl(@PathVariable("roomId") String roomId,
                             @RequestBody UrlDto urlDto, Principal principal) {
        roomService.addUrl(roomId, urlDto.getUrl(), principal);
        return urlDto;
    }

    @GetMapping
    public List<Room> getAllRooms(@RequestParam(required = false, name = "page") int page,
                                  @RequestParam(required = false, name = "sort") String sort) {
        return roomService.findAll(page, sort);
    }

}