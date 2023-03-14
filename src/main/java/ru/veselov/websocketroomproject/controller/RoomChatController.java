package ru.veselov.websocketroomproject.controller;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ru.veselov.websocketroomproject.dto.RoomInfoDTO;
import ru.veselov.websocketroomproject.model.MessageModel;
import ru.veselov.websocketroomproject.model.RoomModel;
import ru.veselov.websocketroomproject.model.SendMessage;
import ru.veselov.websocketroomproject.service.RoomService;

import java.time.LocalDateTime;


@RestController
@Slf4j
@RequestMapping("/api/room/{id}")
public class RoomChatController {

    @Value("${socket.chat-topic}")
    @Setter
    private String chatTopic;
    @Value("${socket.users-topic}")
    @Setter
    private String usersTopic;
    @Value("${socket.video-topic}")
    @Setter
    private String videoTopic;
    @Value("${socket.destination}")
    @Setter
    private String destination;

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final WebSocketConnectionChatListener listener;
    @Autowired
    public RoomChatController(SimpMessagingTemplate messagingTemplate, RoomService roomService, WebSocketConnectionChatListener listener) {
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
        this.listener = listener;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomInfoDTO> sendTopics(@PathVariable("id") String id){
        RoomModel room = roomService.getRoom();
        RoomInfoDTO roomInfoDTO = RoomService.convertToConfigDTO(room);
        String suffix = "/"+room.getId();
        roomInfoDTO.setChatSockTopic(chatTopic+suffix);
        roomInfoDTO.setUserListSockTopic(usersTopic+suffix);
        roomInfoDTO.setVideoSockTopic(videoTopic+suffix);
        roomInfoDTO.setDestination(destination+suffix);
        return new ResponseEntity<>(roomInfoDTO, HttpStatus.OK);
        //FIXME обработка исключения если комната не найдена
    }


    @PatchMapping(value = "/edit",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomInfoDTO> changeName(@RequestBody String name, @PathVariable("id") String id){
        //TODO изменение названия комнаты
        return new ResponseEntity<>(null,HttpStatus.OK);
    }

    @PostMapping(value = "/token/create",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomInfoDTO> createToken(@RequestBody String token, @PathVariable("id") String id){
        //TODO создание токена комнаты
        return new ResponseEntity<>(null,HttpStatus.OK);
    }

    @PatchMapping(value = "/token/refresh/",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomInfoDTO> refreshToken(@RequestBody String newToken, @PathVariable("id") String id){
        //TODO изменение токена комнаты
        return new ResponseEntity<>(null,HttpStatus.OK);
    }


    @MessageMapping("/chat/{id}")//с этого адреса на фронте приходит сообщение
    public void sendMessage(@DestinationVariable("id") String id, MessageModel model){
        log.info("Получено сообщение {}", model);
        messagingTemplate.convertAndSend(chatTopic+"/"+id,
                new SendMessage("Чат № "+id+" привет "+model.getName(),"Сервер", LocalDateTime.now()));
    }



}
