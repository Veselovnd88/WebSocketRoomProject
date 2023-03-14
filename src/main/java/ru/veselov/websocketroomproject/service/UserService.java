package ru.veselov.websocketroomproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.exception.UserNotFoundException;
import ru.veselov.websocketroomproject.model.UserModel;

@Service
@Slf4j
public class UserService {
    public UserModel findUserByUserName(String username) throws UserNotFoundException {
        log.info("Retrieving User {}",username);
        UserModel userModel = new UserModel();
        userModel.setUsername(username);
        return userModel;
    }
}
