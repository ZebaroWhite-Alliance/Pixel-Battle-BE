package ua.cn.stu.pixel_battle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixel_battle.model.Pixel;

@Service
public class RedisService {

    private final RedisTemplate<String, Pixel> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Pixel> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Pixel getPixel(int x, int y) {
        return redisTemplate.opsForValue().get(x + ":" + y);
    }

    public void setPixel(int x, int y, Pixel pixel) {
        redisTemplate.opsForValue().set(x + ":" + y, pixel);
    }
}
