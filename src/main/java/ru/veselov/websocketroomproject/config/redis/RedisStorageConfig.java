package ru.veselov.websocketroomproject.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "redis")
@Component
public class RedisStorageConfig {
    @Getter
    @Setter
    private RedisMasterProperty master;
    @Getter
    @Setter
    private List<RedisReplicaProperty> replicas;

}