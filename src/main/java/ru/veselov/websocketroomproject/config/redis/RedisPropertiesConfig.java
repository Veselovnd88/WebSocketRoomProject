package ru.veselov.websocketroomproject.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "redis")
@Component
@Data
public class RedisPropertiesConfig {

    private RedisMasterProperty master;

    private List<RedisReplicaProperty> replicas;

    private RedisPoolProperty pool;

}