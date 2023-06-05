package ru.veselov.websocketroomproject.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class User {

    private UUID uuid;
    private String username;
    private String email;

}