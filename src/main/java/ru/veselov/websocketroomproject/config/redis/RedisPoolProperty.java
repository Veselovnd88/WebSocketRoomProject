package ru.veselov.websocketroomproject.config.redis;

import lombok.Data;

@Data
public class RedisPoolProperty {

    private int maxTotal;

    private int maxIdle;

    private int minIdle;

}
