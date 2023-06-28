package ru.veselov.websocketroomproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.websocketroomproject.dto.request.RoomSettingsDTO;
import ru.veselov.websocketroomproject.dto.request.UrlDto;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.service.RoomSettingsService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/room")
@Validated
@RequiredArgsConstructor
public class RoomSettingController {

    private final RoomSettingsService roomSettingsService;

    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Room changeSettings(@PathVariable("roomId") @UUID String roomId,
                               @Valid @RequestBody RoomSettingsDTO settings,
                               Principal principal) {
        return roomSettingsService.changeSettings(roomId, settings, principal);
    }

    @PostMapping(value = "/url/{roomId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UrlDto processUrl(@PathVariable("roomId") @UUID String roomId,
                             @Valid @RequestBody UrlDto urlDto,
                             Principal principal) {
        roomSettingsService.addUrl(roomId, urlDto.getUrl(), principal);
        return urlDto;
    }

}
