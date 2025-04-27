package ua.cn.stu.pixel_battle.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class RedisTestController {

    private final StringRedisTemplate redis;

    public RedisTestController(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @GetMapping("/test/redis")
    public String testRedis() {
        redis.opsForValue().set("ping", "pong", Duration.ofSeconds(5));
        String resp = redis.opsForValue().get("ping");
        return "Redis replied: " + resp;
    }
}
