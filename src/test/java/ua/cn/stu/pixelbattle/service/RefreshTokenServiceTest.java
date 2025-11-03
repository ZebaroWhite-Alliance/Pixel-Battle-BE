package ua.cn.stu.pixelbattle.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import ua.cn.stu.pixelbattle.config.JwtProperties;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private StringRedisTemplate redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @Mock
  private JwtProperties jwtProperties;

  @InjectMocks
  private RefreshTokenService refreshTokenService;

  @BeforeEach
  void setUp() {
    lenient().when(jwtProperties.getRefreshTokenDurationMs()).thenReturn(10000L);
    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  @DisplayName("should create a refresh token")
  void createRefreshToken() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

    String token = refreshTokenService.createRefreshToken(userId);

    assertNotNull(token);
    verify(valueOperations).set(eq("refresh:" + token), eq(userId.toString()), any(Duration.class));
  }

  @Test
  @DisplayName("should throw when user not found")
  void createRefreshTokenUserNotFound() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    RuntimeException ex =
        assertThrows(RuntimeException.class,
            () -> refreshTokenService.createRefreshToken(userId));
    assertEquals("User not found", ex.getMessage());
  }

  @Test
  @DisplayName("should verify token expiration")
  void verifyExpiration() {
    Long userId = 1L;
    String token = "token123";
    when(valueOperations.get("refresh:" + token)).thenReturn(userId.toString());

    Long result = refreshTokenService.verifyExpiration(token);

    assertEquals(userId, result);
  }

  @Test
  @DisplayName("should throw when token expired")
  void verifyExpirationExpired() {
    String token = "token123";
    when(valueOperations.get("refresh:" + token)).thenReturn(null);

    RuntimeException ex =
        assertThrows(RuntimeException.class,
            () -> refreshTokenService.verifyExpiration(token));
    assertEquals("Refresh token expired or invalid", ex.getMessage());
  }

  @Test
  @DisplayName("should delete token by value")
  void deleteByToken() {
    String token = "token123";

    refreshTokenService.deleteByToken(token);

    verify(redisTemplate).delete("refresh:" + token);
  }

  @Test
  @DisplayName("should delete all tokens for user")
  void deleteByUserId() {
    var keys = Set.of("refresh:token1", "refresh:token2", "refresh:token3");
    when(redisTemplate.keys("refresh:*")).thenReturn(keys);
    when(redisTemplate.opsForValue().get("refresh:token1")).thenReturn("1");
    when(redisTemplate.opsForValue().get("refresh:token2")).thenReturn("2");
    when(redisTemplate.opsForValue().get("refresh:token3")).thenReturn("1");

    refreshTokenService.deleteByUserId(1L);

    verify(redisTemplate).delete("refresh:token1");
    verify(redisTemplate, never()).delete("refresh:token2");
    verify(redisTemplate).delete("refresh:token3");
  }

}
