package ru.veselov.websocketroomproject.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.veselov.websocketroomproject.config.redis.RedisMasterProperty;
import ru.veselov.websocketroomproject.config.redis.RedisPoolProperty;
import ru.veselov.websocketroomproject.config.redis.RedisPropertiesConfig;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class RedisConfiguration {

    private final RedisPropertiesConfig redisPropertiesConfig;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build();
        RedisStaticMasterReplicaConfiguration masterConfig = createRedisMasterReplicaConfig();

        return new LettuceConnectionFactory(masterConfig, clientConfig);
    }

    @Bean(destroyMethod = "shutdown")
    ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public ClientOptions clientOptions() {
        return ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT)
                .autoReconnect(true)
                .build();
    }

    @Bean
    LettucePoolingClientConfiguration lettucePoolConfig(ClientOptions options, ClientResources dcr) {

        return LettucePoolingClientConfiguration.builder()
                .poolConfig(createGenericObjectPoolConfig())
                .clientOptions(options)
                .clientResources(dcr)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setStringSerializer(new StringRedisSerializer());
        template.setEnableTransactionSupport(true);
        return template;
    }

    private RedisStaticMasterReplicaConfiguration createRedisMasterReplicaConfig() {
        RedisMasterProperty master = redisPropertiesConfig.getMaster();
        final RedisStaticMasterReplicaConfiguration masterConfig =
                new RedisStaticMasterReplicaConfiguration(master.getHost(), master.getPort());
        masterConfig.setPassword(RedisPassword.of(redisPropertiesConfig.getMaster().getPassword()));
        redisPropertiesConfig.getReplicas()
                .forEach(replica -> masterConfig.addNode(replica.getHost(), replica.getPort())
                );
        return masterConfig;
    }

    private GenericObjectPoolConfig createGenericObjectPoolConfig() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        RedisPoolProperty poolProperties = redisPropertiesConfig.getPool();
        poolConfig.setMaxIdle(poolProperties.getMaxIdle());
        poolConfig.setMaxTotal(poolProperties.getMaxTotal());
        poolConfig.setMinIdle(poolProperties.getMinIdle());
        return poolConfig;
    }

}