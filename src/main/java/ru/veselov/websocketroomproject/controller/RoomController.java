package ru.veselov.websocketroomproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.annotation.SortingParam;
import ru.veselov.websocketroomproject.dto.request.SortParameters;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Get room by Id (UUID)", description = "Retrieve Room by id from database")
    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable("roomId") @UUID String id,
                        @RequestParam(required = false, name = "token") String token) {
        return roomService.getRoomById(id, token);
    }

    @Operation(summary = "Create room", description = "Create room and save to database")
    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@Valid @RequestBody Room room, Principal principal) {
        return roomService.createRoom(room, principal);
    }

    @Operation(summary = "Get all public rooms", description = "Retrieve rooms from database")
    @GetMapping("/all")
    public List<Room> getAllRooms(@Valid @SortingParam SortParameters parameters) {
        return roomService.findAll(parameters.getPage(), parameters.getSort(), parameters.getOrder());
    }

    @Operation(summary = "Get Room by Tag and sorting parameters", description = "Get rooms from database")
    @GetMapping("/all/{tag}")
    public List<Room> getRoomsByTag(@PathVariable String tag, @Valid @SortingParam SortParameters parameters) {
        return roomService.findAllByTag(tag, parameters.getPage(), parameters.getSort(), parameters.getOrder());
    }

}
