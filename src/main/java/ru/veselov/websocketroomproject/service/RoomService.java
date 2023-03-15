package ru.veselov.websocketroomproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.User;

import java.util.Date;

@Service
@Slf4j
public class RoomService {
    public Room findRoomById(Integer id) throws RoomNotFoundException {
        log.info("Retrieving room #{} from db", id);
        //This model just for testing in browser
        return new Room(
                id,
                "testRoom",
                true,
                "url",
                "token",
                new Date(),
                new User(1, "name", "email"));
    }
}
