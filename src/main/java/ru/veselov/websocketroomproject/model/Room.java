package ru.veselov.websocketroomproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Room {
    private Integer id;
    private String name;
    private Boolean isPublic;
    private String sourceUrl;
    private String roomToken;
    private Date deleteTime;
    private User owner;
}
