package dev.kovaliv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.function.Function;

import static dev.kovaliv.config.ContextConfig.REDIS_PROFILE;
import static dev.kovaliv.config.ContextConfig.context;
import static dev.kovaliv.utils.GsonUtils.gson;
import static java.util.Objects.requireNonNullElse;
import static java.util.stream.Collectors.toMap;

@Profile(REDIS_PROFILE)
@Configuration
public class Redis {

    @Bean
    public JedisPool getJedisPool() {
        return new JedisPool(
                System.getenv("REDIS_HOST"),
                Integer.parseInt(requireNonNullElse(System.getenv("REDIS_PORT"), "6379")),
                null, System.getenv("REDIS_PASSWORD")
        );
    }
    
    public static JedisPool redis() {
        return context().getBean(JedisPool.class);
    }
    
    public static <T> T get(String queue, String key, Function<String, T> mappingFunction, Class<T> clazz) {
        try (var jedis = redis().getResource()) {
            String value = jedis.hget(queue, key);
            if (value == null) {
                T countedValue = mappingFunction.apply(key);
                jedis.hset(queue, key, gson().toJson(countedValue));
                return countedValue;
            } else {
                return gson().fromJson(value, clazz);
            }
        }
    }

    public static <T> Map<String, T> get(String queue, Class<T> clazz) {
        try (var jedis = redis().getResource()) {
            return jedis.hgetAll(queue).entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> gson().fromJson(e.getValue(), clazz)));
        }
    }

    public static void clean(String queue, String key) {
        try (var jedis = redis().getResource()) {
            jedis.hdel(queue, key);
        }
    }

    public static void clean(String queue) {
        try (var jedis = redis().getResource()) {
            jedis.del(queue);
        }
    }
}
