package ru.veselov.websocketroomproject.controller;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.veselov.websocketroomproject.cache.ChatUsersCache;
import ru.veselov.websocketroomproject.cache.SessionCache;
import ru.veselov.websocketroomproject.dto.ChatMessageDTO;
import ru.veselov.websocketroomproject.dto.ChatUserDTO;
import ru.veselov.websocketroomproject.dto.MessageType;
import ru.veselov.websocketroomproject.exception.NoRoomFoundException;
import ru.veselov.websocketroomproject.exception.NoUserFoundException;
import ru.veselov.websocketroomproject.model.ChatUser;
import ru.veselov.websocketroomproject.model.RoomModel;
import ru.veselov.websocketroomproject.model.UserModel;
import ru.veselov.websocketroomproject.service.RoomService;
import ru.veselov.websocketroomproject.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class WebSocketConnectionChatListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatUsersCache cache;
    private final SessionCache sessionCache;
    private final RoomService roomService;
    private final UserService userService;

    @Value("${socket.users-topic}")
    @Setter
    private String usersTopic;

    @Value("${socket.chat-topic}")
    @Setter
    private String chatTopic;
    @Autowired
    public WebSocketConnectionChatListener(SimpMessagingTemplate messagingTemplate, ChatUsersCache cache, SessionCache sessionCache, RoomService roomService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.cache = cache;
        this.sessionCache = sessionCache;
        this.roomService = roomService;
        this.userService = userService;
    }

    @EventListener
    public void handleSubscribeUser(SessionSubscribeEvent session) {
        /*Если пользователь аутентифицирован то он прикрепляется в объекте сессии
         FIXME проверить еще раз когда подключим окончательно security, в любом случае нам нужен только индивидуальный юзернейм
         для отправки запроса на сервис с бд юзеров для получения информации
        */


        if(session.getUser()==null){
            log.error("No authenticated user in session");
            return;
        }
        String username = session.getUser().getName();
        UserModel userModel;
        try {
            userModel = userService.findUserByUserName(username);
        } catch (NoUserFoundException e) {
            log.error("User with such username {} not found : {}", username,e.getMessage());
            return;
        }
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(session.getMessage());
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        if(destination==null){
            log.error("Topic is null");
            return;
        }
        if (destination.startsWith(usersTopic)) {
            Integer roomId;
            try{
                roomId = getRoomId(destination);
            }
            catch (NumberFormatException e){
                log.error("Not correct room number in destination {}, {}",destination,e.getMessage());
                return;
            }
            RoomModel roomById;
            try {
                roomById = roomService.findRoomById(roomId);
            } catch (NoRoomFoundException e) {
                log.error("No room with this ID: {}, {}",roomId,e.getMessage());
                return;
            }
            ChatUser chatUser = new ChatUser(userModel.getId(), roomId, sessionId, userModel.getUsername(), destination,
                    roomById.getOwner().getId().equals(userModel.getId()));
            log.info("User {} with session {} connected to topic {}", username, sessionId, destination);
            cache.addUser(roomId, chatUser);
            sessionCache.addSessionId(sessionId, roomId);
            //Сортировка, чтобы оунер всегда был первым
            List<ChatUserDTO> users = getUsers(roomId);
            messagingTemplate.convertAndSend(usersTopic + "/" + roomId, users);
            messagingTemplate.convertAndSend(chatTopic + "/" + roomId,
                    new ChatMessageDTO("Пользователь " + username + " присоединился к чату",
                                "Комната №" + roomId, MessageType.SERVER, LocalDateTime.now()));
            }
        else {
            log.trace("Not correct topic for answer: {}", destination);
        }
    }


    @EventListener
    public void handleDisconnectUser(SessionDisconnectEvent session) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(session.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Integer roomId = sessionCache.getRoom(sessionId);
        String userName = cache.getRoomUsers(roomId).get(sessionId).getUserName();
        if(roomId!=null){
            cache.removeUser(roomId,sessionId);
            sessionCache.removeSessionId(sessionId);
            if(cache.getRoomUsers(roomId)!=null){
                messagingTemplate.convertAndSend(chatTopic+"/"+roomId,
                        new ChatMessageDTO("Пользователь "+userName+" покинул чат",
                                "Комната №"+roomId, MessageType.SERVER, LocalDateTime.now()));
                List<ChatUserDTO> users = getUsers(roomId);
                messagingTemplate.convertAndSend(usersTopic+"/"+roomId,users);}
            }
    }

    private Integer getRoomId(String destination) {
        String[] split;
        split=destination.split("/");
        return Integer.valueOf(split[split.length-1]);
    }

    private List<ChatUserDTO> getUsers(Integer roomId){
        return cache.getRoomUsers(roomId).values()
                .stream().map(ChatUserDTO::convertToChatUserDTO)
                .sorted((x, y) -> y.getIsOwner().compareTo(x.getIsOwner()))
                .collect(Collectors.toList());
    }

    @Profile("test")
    public List<ChatUserDTO> checkSorting(Integer roomId){
        return getUsers(roomId);
    }

}
