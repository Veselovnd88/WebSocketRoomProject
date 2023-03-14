package ru.veselov.websocketroomproject.cache.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.veselov.websocketroomproject.cache.ChatUsersCache;
import ru.veselov.websocketroomproject.model.ChatUser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class ChatUsersCacheImpl implements ChatUsersCache {
    /*В кеше ключ-id комнаты, значение мапа - id сессии и сам юзер*/
    Map<Integer, Map<String,ChatUser>> cache = new HashMap<>();
    @Override
    public void addUser(Integer roomId, ChatUser chatUser) {
        log.trace("User {} added to the room #{}", chatUser.getUserName(), roomId);
        if(cache.containsKey(roomId)){
            cache.get(roomId).put(chatUser.getSession(),chatUser);
        }
        else{
            Map<String,ChatUser> users = new LinkedHashMap<>();
            users.put(chatUser.getSession(), chatUser);
            cache.put(roomId,users);
        }
    }

    @Override
    public void removeUser(Integer roomId,String sessionId) {
        log.trace("User with session #{} removed from room #{}",sessionId, roomId);
        Map<String, ChatUser> users = cache.get(roomId);
        if(users!=null){
            users.remove(sessionId);
            if(users.isEmpty()){
                removeRoom(roomId);
            }
        }
    }

    @Override
    public Map<String,ChatUser> getRoomUsers(Integer roomId) {
        Map<String, ChatUser> stringChatUserMap = cache.get(roomId);
        //stringChatUserMap.put("testSession", new ChatUser(2,9,"testSession",
          //      "Petya","testDestination",false));//FIXME
        return stringChatUserMap;
    }

    @Override
    public void removeRoom(Integer roomId) {
        log.info("Room #{} removed from cache",roomId);
        cache.remove(roomId);
    }

    @Override
    public void clear() {
        cache.clear();
    }

}
