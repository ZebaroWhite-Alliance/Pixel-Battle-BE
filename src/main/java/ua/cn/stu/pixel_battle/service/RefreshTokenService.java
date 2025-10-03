package ua.cn.stu.pixel_battle.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixel_battle.config.JwtProperties;
import ua.cn.stu.pixel_battle.repository.UserRepository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

  private final StringRedisTemplate redisTemplate;
  private final UserRepository userRepository;
  private final long refreshTokenDurationMs;

  public RefreshTokenService(UserRepository userRepository,
                             StringRedisTemplate redisTemplate,
                             JwtProperties jwtProperties) {
    this.userRepository = userRepository;
    this.redisTemplate = redisTemplate;
    this.refreshTokenDurationMs = jwtProperties.getRefreshTokenDurationMs();
  }

  public String createRefreshToken(Long userId) {

    userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    String token = UUID.randomUUID().toString();
    ValueOperations<String, String> ops = redisTemplate.opsForValue();

    ops.set("refresh:" + token, userId.toString(), Duration.ofMillis(refreshTokenDurationMs));

    return token;
  }

  public Long verifyExpiration(String  token) {
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    String userId = ops.get("refresh:" + token);

    if (userId == null) {
      throw new RuntimeException("Refresh token expired or invalid");
    }

    return Long.valueOf(userId);
  }

  public Optional<Long> findByToken(String token) {
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    String userId = ops.get("refresh:" + token);
    return userId != null ? Optional.of(Long.valueOf(userId)) : Optional.empty();
  }

  public void deleteByToken(String token) {
    redisTemplate.delete("refresh:" + token);
  }

  public void deleteByUserId(Long userId) {

  }
  }
