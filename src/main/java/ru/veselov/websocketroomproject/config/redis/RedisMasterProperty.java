package ru.veselov.websocketroomproject.config.redis;

import lombok.Data;

@Data
public class RedisMasterProperty {

    private int port;

    private String host;

    private String password;

}