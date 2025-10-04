package ua.cn.stu.pixelbattle.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.dto.PixelResponse;
import ua.cn.stu.pixelbattle.exception.RateLimitException;
import ua.cn.stu.pixelbattle.model.Pixel;
import ua.cn.stu.pixelbattle.model.PixelHistory;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.PixelHistoryRepository;
import ua.cn.stu.pixelbattle.repository.UserRepository;




/**
 * Service for managing pixels on the field and tracking their history.
 *
 * <p>Provides functionality to:
 * <ul>
 *     <li>Get pixel data from Redis</li>
 *     <li>Change pixel color with rate limiting</li>
 *     <li>Track pixel changes in history</li>
 *     <li>Retrieve all pixels from Redis</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PixelService {
  private final RedisTemplate<String, Pixel> redisTemplate;
  private final StringRedisTemplate stringRedisTemplate;
  private final PixelHistoryRepository pixelHistoryRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;


  private static final int FIELD_WIDTH = 1000;
  private static final int FIELD_HEIGHT = 1000;
  private static final String USER_RATE_KEY_PREFIX = "user:rate:";

  /**
   * Retrieves a pixel from Redis by coordinates.
   *
   * @param x the X coordinate of the pixel
   * @param y the Y coordinate of the pixel
   * @return the {@link Pixel} object at the given coordinates, or null if not set
   */
  public Pixel getPixel(int x, int y) {
    String key = "pixel:" + x + ":" + y;
    return redisTemplate.opsForValue().get(key);
  }

  /**
   * Changes the color of a pixel, records the change in history,
   * and applies rate limiting for non-admin users.
   *
   * @param x        the X coordinate of the pixel
   * @param y        the Y coordinate of the pixel
   * @param newColor the new color to set
   * @param userId   the ID of the user making the change
   * @throws IllegalArgumentException if coordinates are out of bounds or user is not found
   * @throws RateLimitException       if a non-admin user tries to change more than once per minute
   */
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

    String key = "pixel:" + x + ":" + y;
    Pixel old = redisTemplate.opsForValue().get(key);
    String oldColor = old != null ? old.getColor() : "#FFFFFF";

    // if old color is the same then ignore
    if (oldColor.equalsIgnoreCase(newColor)) {
      return;
    }
    user.incrementPixelChanges();
    userRepository.save(user);

    PixelHistory history = new PixelHistory(x, y, oldColor, newColor, user);
    pixelHistoryRepository.save(history);

    Pixel newPixel = new Pixel(x, y, newColor, user.getUsername());
    redisTemplate.opsForValue().set(key, newPixel);

    PixelResponse response = new PixelResponse(x, y, newColor, user.getUsername());
    messagingTemplate.convertAndSend("/topic/pixels", response);
  }

  /**
   * Retrieves all pixels from Redis and converts them to DTOs.
   *
   * @return list of {@link PixelResponse} containing pixel coordinates, color, and username
   * @throws RuntimeException if unable to connect to Redis or retrieve pixel data
   */
  public List<PixelResponse> getAllPixels() {
    try {
      Set<String> keys = redisTemplate.keys("pixel:*");
      if (keys == null) {
        return Collections.emptyList();
      }

      return keys.stream()
          .map(k -> {
            try {
              return redisTemplate.opsForValue().get(k);
            } catch (Exception e) {
              throw new RuntimeException(
                  "Failed to get key " + k + " from Redis: " + e.getMessage(), e);
            }
          })
          .filter(Objects::nonNull)
          .map(p -> new PixelResponse(p.getX(), p.getY(), p.getColor(), p.getUsername()))
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException("Unable to connect to Redis: " + e.getMessage(), e);
    }
  }
}
