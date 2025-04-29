package ua.cn.stu.pixel_battle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixel_battle.dto.PixelResponse;
import ua.cn.stu.pixel_battle.exception.RateLimitException;
import ua.cn.stu.pixel_battle.model.Pixel;
import ua.cn.stu.pixel_battle.model.PixelHistory;
import ua.cn.stu.pixel_battle.model.User;
import ua.cn.stu.pixel_battle.repository.PixelHistoryRepository;
import ua.cn.stu.pixel_battle.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class PixelService {
    private final RedisTemplate<String, Pixel> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final PixelHistoryRepository pixelHistoryRepository;
    private final UserRepository userRepository;


    private static final int FIELD_WIDTH = 1000;
    private static final int FIELD_HEIGHT = 1000;


    private static final String USER_RATE_KEY_PREFIX = "user:rate:";

    @Autowired
    public PixelService(RedisTemplate<String, Pixel> redisTemplate,
                        StringRedisTemplate stringRedisTemplate,
                        PixelHistoryRepository pixelHistoryRepository,
                        UserRepository userRepository) {

        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.pixelHistoryRepository = pixelHistoryRepository;
        this.userRepository = userRepository;
    }

    public Pixel getPixel(int x, int y) {
        String key = "pixel:" + x + ":" + y;
        return redisTemplate.opsForValue().get(key);
    }

    public void changePixel(int x, int y, String newColor, Long userId) {
        if (x < 0 || x >= FIELD_WIDTH || y < 0 || y >= FIELD_HEIGHT) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            String rateKey = USER_RATE_KEY_PREFIX + userId;
            Boolean allowed = stringRedisTemplate.opsForValue()
                    .setIfAbsent(rateKey, "1", 1, TimeUnit.MINUTES);

            if (Boolean.FALSE.equals(allowed)) {
                throw new RateLimitException("You can change pixel only once per minute");
            }
        }
        user.incrementPixelChanges();
        userRepository.save(user);
        String key = "pixel:" + x + ":" + y;
        Pixel old = redisTemplate.opsForValue().get(key);
        String oldColor = old != null ? old.getColor() : "#FFFFFF";

        PixelHistory history = new PixelHistory(x, y, oldColor, newColor, user);
        pixelHistoryRepository.save(history);

        Pixel newPixel = new Pixel(x, y, newColor);
        redisTemplate.opsForValue().set(key, newPixel);
    }


    public List<PixelResponse> getAllPixels() {
        Set<String> keys = redisTemplate.keys("pixel:*");
        return keys.stream()
                .map(k -> redisTemplate.opsForValue().get(k))
                .filter(Objects::nonNull)
                .map(p -> new PixelResponse(p.getX(), p.getY(), p.getColor()))
                .toList();
    }
}
