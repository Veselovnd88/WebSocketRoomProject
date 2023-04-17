package ru.veselov.websocketroomproject.config.redis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisMasterProperty {
    private int port;
    private String host;
    private String password;
}