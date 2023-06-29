package ru.veselov.websocketroomproject.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.websocketroomproject.entity.TagEntity;
import ru.veselov.websocketroomproject.mapper.TagMapper;
import ru.veselov.websocketroomproject.mapper.TagMapperImpl;
import ru.veselov.websocketroomproject.model.Tag;
import ru.veselov.websocketroomproject.repository.TagRepository;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagServiceImpl tagService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(tagService, "tagMapper", new TagMapperImpl(), TagMapper.class);
    }

    @Test
    void shouldReturnTagSet() {
        Mockito.when(tagRepository.findAll()).thenReturn(List.of(
                new TagEntity("1"),
                new TagEntity("2")
        ));

        Set<Tag> tags = tagService.getTags();

        Assertions.assertThat(tags).hasSize(2).contains(new Tag("1"), new Tag("2"));
    }

}
