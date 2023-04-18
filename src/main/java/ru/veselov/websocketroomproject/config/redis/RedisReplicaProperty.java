package ru.veselov.websocketroomproject.config.redis;

import lombok.Data;

@Data
public class RedisReplicaProperty {

    private int port;

    private String host;
}
