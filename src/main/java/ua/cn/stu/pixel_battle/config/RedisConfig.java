package ua.cn.stu.pixel_battle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import ua.cn.stu.pixel_battle.model.Pixel;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Pixel> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Pixel> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer<Pixel> valueSerializer = new Jackson2JsonRedisSerializer<>(Pixel.class);
        template.setValueSerializer(valueSerializer);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
