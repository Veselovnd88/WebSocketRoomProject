package ru.veselov.websocketroomproject.repository;

import org.springframework.stereotype.Repository;
import ru.veselov.websocketroomproject.model.RoomModel;
import ru.veselov.websocketroomproject.model.TagModel;
import ru.veselov.websocketroomproject.model.UserModel;

import java.util.*;

@Repository
public class RoomRepository {

    private final List<RoomModel> rooms = new ArrayList<>(List.of(
            RoomModel.builder().roomTags(
                            Set.of(new TagModel(1,"TestTag"),
                                    new TagModel(2,"TestTag2"))

                    )
                    .roomToken("token")
                    .id(1)
                    .deleteTime(new Date())
                    .isPublic(true)
                    .name("MyRoom")
                    .sourceUrl("sourceUrl")
                    .owner(new UserModel(100,"Vasya","email"))
                    .build()
    ));

    public RoomModel find(){
        return rooms.get(0);
    }


    public Optional<RoomModel> findById(Integer id) {
        return Optional.ofNullable(
                RoomModel.builder().roomTags(
                                Set.of(new TagModel(1,"TestTag"),
                                        new TagModel(2,"TestTag2"))

                        )
                        .roomToken("token")
                        .id(id)
                        .deleteTime(new Date())
                        .isPublic(true)
                        .name("MyRoom")
                        .sourceUrl("sourceUrl")
                        .owner(new UserModel(100,"Vasya","email"))
                        .build());
    }
}
