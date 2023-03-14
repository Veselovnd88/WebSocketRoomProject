package ru.veselov.websocketroomproject.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;
import ru.veselov.websocketroomproject.model.RoomModel;
import ru.veselov.websocketroomproject.model.UserModel;

@Service
@Slf4j
public class RoomService {
    public RoomModel findRoomById(Integer id) throws RoomNotFoundException {
        log.info("Retrieving room #{} from db", id);
        RoomModel roomModel = new RoomModel();
        roomModel.setOwner(new UserModel(
                1,
                "name",
                "email;"
        ));
        return roomModel;
    }
}
