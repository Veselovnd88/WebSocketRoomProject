package ru.veselov.websocketroomproject.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UrlModel {
    private RoomModel roomModel;
    private String sourceUrl;
}
