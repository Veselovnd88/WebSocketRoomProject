package ru.veselov.websocketroomproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RoomInfoDTO {
    private String chatSockTopic;
    private String userListSockTopic;
    private String destination;
    private String videoSockTopic;
    private Boolean isPublic;

    private Integer id;
    private String name;
    private String url;
    @JsonProperty("owner")
    private String ownerName;

}
