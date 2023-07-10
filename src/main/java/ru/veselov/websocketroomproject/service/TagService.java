package ru.veselov.websocketroomproject.service;

import ru.veselov.websocketroomproject.model.Tag;

import java.util.Set;

public interface TagService {

    Set<Tag> getTags();

    Set<Tag> deleteTag(String name);

    Set<Tag> addTag(String name);

}
