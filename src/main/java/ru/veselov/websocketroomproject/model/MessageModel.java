package ru.veselov.websocketroomproject.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MessageModel {
    /*This is message format from sender, it will be convert to this fields*/
    private String name;

}
