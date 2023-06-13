package ru.veselov.websocketroomproject.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.annotation.OrderDirection;
import ru.veselov.websocketroomproject.annotation.SortBy;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.validation.FieldValidationResponseService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/room")
@CrossOrigin
@Validated
@Slf4j
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    private final FieldValidationResponseService fieldValidationResponseService;

    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable("roomId") @UUID String id,
                        @RequestParam(required = false, name = "token") String token) {
        return roomService.getRoomById(id, token);
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@Valid @RequestBody Room room, BindingResult bindingResult, Principal principal) {
        fieldValidationResponseService.validateFields(bindingResult);
        return roomService.createRoom(room, principal);
    }

    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Room changeSettings(@PathVariable("roomId") @UUID String roomId,
                               @Valid @RequestBody RoomSettingsDTO settings,
                               BindingResult bindingResult,
                               Principal principal) {
        fieldValidationResponseService.validateFields(bindingResult);
        return roomService.changeSettings(roomId, settings, principal);
    }


    @PostMapping(value = "/url/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UrlDto processUrl(@PathVariable("roomId") @UUID String roomId,
                             @Valid @RequestBody UrlDto urlDto,
                             BindingResult bindingResult,
                             Principal principal) {
        fieldValidationResponseService.validateFields(bindingResult);
        roomService.addUrl(roomId, urlDto.getUrl(), principal);
        return urlDto;
    }

    @GetMapping
    public List<Room> getAllRooms(@RequestParam(required = false, name = "page") @PositiveOrZero int page,
                                  @RequestParam(required = false, name = "sort") @SortBy String sort,
                                  @RequestParam(required = false, name = "order") @OrderDirection String order) {
        return roomService.findAll(page, sort, order);
    }

}
