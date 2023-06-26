package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.websocketroomproject.entity.RoomEntity;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.model.Room;
import ru.veselov.websocketroomproject.model.Tag;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TagMapper.class})
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {

    Room entityToRoom(RoomEntity roomEntity); //TODO check for correct mapping Tags

    @Mapping(target = "urls", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "isPrivate", source = "isPrivate", defaultValue = "false")
    RoomEntity toEntity(Room room);

    List<Room> entitiesToRooms(List<RoomEntity> entityList);

}