package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.Mapper;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.model.Room;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomEntity toEntity(Room room);

    Room entityToRoom(RoomEntity roomEntity);

    RoomEntity dtoToRoomEntity(Room room);

    List<Room> entitiesToRooms(List<RoomEntity> entityList);

}
