package ua.cn.stu.pixelbattle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ua.cn.stu.pixelbattle.model.Pixel;

/**
 * Configuration class for Redis beans.
 *
 * <p>Provides {@link RedisTemplate} for storing {@link Pixel} objects
 * and {@link StringRedisTemplate} for general String operations.
 */
@Configuration
public class RedisConfig {

  /**
   * Creates a {@link RedisTemplate} for storing {@link Pixel} objects in Redis.
   *
   * <p>Configures JSON serialization for values and String serialization for keys.
   *
   * @param factory the Redis connection factory
   * @return configured RedisTemplate
   */
  @Bean
  public RedisTemplate<String, Pixel> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Pixel> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    // values
    Jackson2JsonRedisSerializer<Pixel> valueSerializer =
        new Jackson2JsonRedisSerializer<>(Pixel.class);
    template.setValueSerializer(valueSerializer);

    // keys
    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    template.setKeySerializer(stringRedisSerializer);
    return template;
  }

  /**
   * Creates a {@link StringRedisTemplate} for general String operations in Redis.
   *
   * @param redisConnectionFactory the Redis connection factory
   * @return configured StringRedisTemplate
   */
  @Bean
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    return new StringRedisTemplate(redisConnectionFactory);
  }
}
