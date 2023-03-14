package ru.veselov.websocketroomproject.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserModel {
    private Integer id;
    private String username;
    private String email;

}
