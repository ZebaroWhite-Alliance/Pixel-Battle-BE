package ua.cn.stu.pixelbattle.service;

import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.config.JwtProperties;
import ua.cn.stu.pixelbattle.repository.UserRepository;

/**
 * Service for managing refresh tokens stored in Redis.
 *
 * <p>Provides functionality to:
 * <ul>
 *     <li>Create refresh tokens for users</li>
 *     <li>Verify token expiration</li>
 *     <li>Find tokens and associated users</li>
 *     <li>Delete tokens individually or by user</li>
 * </ul>
 */
@Service
public class RefreshTokenService {

  private final StringRedisTemplate redisTemplate;
  private final UserRepository userRepository;
  private final long refreshTokenDurationMs;

  /**
   * Constructs the {@code RefreshTokenService}.
   *
   * @param userRepository the repository for accessing user data
   * @param redisTemplate  the Redis template for token storage
   * @param jwtProperties  properties containing refresh token duration
   */
  public RefreshTokenService(UserRepository userRepository,
                             StringRedisTemplate redisTemplate,
                             JwtProperties jwtProperties) {
    this.userRepository = userRepository;
    this.redisTemplate = redisTemplate;
    this.refreshTokenDurationMs = jwtProperties.getRefreshTokenDurationMs();
  }

  /**
   * Creates a new refresh token for a specific user and stores it in Redis.
   *
   * <p>The token is automatically set to expire after the duration specified in
   * {@link JwtProperties#getRefreshTokenDurationMs()}.
   *
   * @param userId the ID of the user for whom the token is generated
   * @return the generated refresh token string
   * @throws RuntimeException if the user with the given ID does not exist
   */
  public String createRefreshToken(Long userId) {

    userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    String token = UUID.randomUUID().toString();
    ValueOperations<String, String> ops = redisTemplate.opsForValue();

    ops.set("refresh:" + token, userId.toString(), Duration.ofMillis(refreshTokenDurationMs));

    return token;
  }

  /**
   * Verifies that a given refresh token exists and has not expired.
   *
   * <p>If the token is valid, returns the associated user ID.
   * If the token is expired or invalid, throws a {@code RuntimeException}.
   *
   * @param token the refresh token string to verify
   * @return the user ID associated with the token
   * @throws RuntimeException if the token is expired or invalid
   */
  public Long verifyExpiration(String token) {
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    String userId = ops.get("refresh:" + token);

    if (userId == null) {
      throw new RuntimeException("Refresh token expired or invalid");
    }

    return Long.valueOf(userId);
  }

  /**
   * Deletes a specific refresh token from Redis.
   *
   * @param token the refresh token to delete
   */
  public void deleteByToken(String token) {
    redisTemplate.delete("refresh:" + token);
  }

  /**
   * Deletes all refresh tokens associated with a given user ID.
   *
   * <p>This method iterates over all stored tokens and removes those matching
   * the specified user ID. Useful for invalidating all previous sessions
   * when a user logs in or changes their password.
   *
   * @param userId the ID of the user whose tokens should be deleted
   */
  public void deleteByUserId(Long userId) {
    var keys = redisTemplate.keys("refresh:*"); // все токены
    if (keys != null) {
      keys.forEach(key -> {
        String value = redisTemplate.opsForValue().get(key);
        if (value != null && value.equals(userId.toString())) {
          redisTemplate.delete(key);
        }
      });
    }
  }
}
