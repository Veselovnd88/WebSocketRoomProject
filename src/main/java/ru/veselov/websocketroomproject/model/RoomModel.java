package ru.veselov.websocketroomproject.model;

import lombok.*;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RoomModel {

    private Integer id;
    private String name;
    private Boolean isPublic;
    private String sourceUrl;
    private String roomToken;
    private Date deleteTime;
    private UserModel owner;

    @Builder.Default private Set<TagModel> roomTags= new HashSet<>();

    @Builder.Default private List<String> users = new ArrayList<>();

    @Builder.Default private List<UrlModel> urlHistory = new LinkedList<>();

    @Builder.Default private List<MessageModel> messages = new ArrayList<>();

}
