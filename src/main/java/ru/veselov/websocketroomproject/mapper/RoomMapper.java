package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.Mapper;
import ru.veselov.websocketroomproject.dto.RoomCreationDTO;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.model.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomEntity toEntity(Room room);

    Room entityToRoom(RoomEntity roomEntity);

    RoomEntity dtoToRoomEntity(Room room);

}
