package ru.veselov.websocketroomproject.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String email;
}
