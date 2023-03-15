package ru.veselov.websocketroomproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.exception.UserNotFoundException;
import ru.veselov.websocketroomproject.model.User;

@Service
@Slf4j
public class UserService {
    public User findUserByUserName(String username) throws UserNotFoundException {
        log.info("Retrieving User {}", username);
        //Model just for testing in browser
        return new User(1, username, "email");
    }
}
