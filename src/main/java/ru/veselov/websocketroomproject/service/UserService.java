package ru.veselov.websocketroomproject.service;

import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.exception.NoUserFoundException;
import ru.veselov.websocketroomproject.model.UserModel;

@Service
public class UserService {

    public UserModel findUserByUserName(String username) throws NoUserFoundException {
        //TODO WebClient to get user information from REST
        UserModel userModel = new UserModel();
        userModel.setId(100);
        userModel.setUsername(username);
        return userModel;
    }
}
