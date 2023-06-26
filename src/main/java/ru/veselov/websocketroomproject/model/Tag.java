package ru.veselov.websocketroomproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Tag implements Serializable {

    private String name;

}
