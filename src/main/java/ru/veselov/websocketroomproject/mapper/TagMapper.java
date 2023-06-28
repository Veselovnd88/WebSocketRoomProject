package ru.veselov.websocketroomproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.model.Tag;

import java.util.Set;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "tagId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    Set<Tag> toTags(Set<TagEntity> entitySet);

}
