package ru.veselov.websocketroomproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.annotation.SortingParam;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.SortParameters;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
@CrossOrigin
@Validated
@Slf4j
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable("roomId") @UUID String id,
                        @RequestParam(required = false, name = "token") String token) {
        return roomService.getRoomById(id, token);
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@Valid @RequestBody Room room, Principal principal) {
        return roomService.createRoom(room, principal);
    }

    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Room changeSettings(@PathVariable("roomId") @UUID String roomId,
                               @Valid @RequestBody RoomSettingsDTO settings,
                               Principal principal) {
        return roomService.changeSettings(roomId, settings, principal);
    }

    @PostMapping(value = "/url/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UrlDto processUrl(@PathVariable("roomId") @UUID String roomId,
                             @Valid @RequestBody UrlDto urlDto,
                             Principal principal) {
        roomService.addUrl(roomId, urlDto.getUrl(), principal);
        return urlDto;
    }

    @GetMapping("/all")
    public List<Room> getAllRooms(@Valid @SortingParam SortParameters parameters) {
        return roomService.findAll(parameters.getPage(), parameters.getSort(), parameters.getOrder());
    }

}
