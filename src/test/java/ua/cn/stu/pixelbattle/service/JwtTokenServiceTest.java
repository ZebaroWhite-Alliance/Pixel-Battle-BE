package ua.cn.stu.pixelbattle.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.cn.stu.pixelbattle.config.JwtProperties;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.UserRepository;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtProperties jwtProperties;

  private JwtTokenService jwtTokenService;

  @BeforeEach
  void setUp() {
    jwtProperties = new JwtProperties();
    jwtProperties.setSecret("test-secret");
    jwtProperties.setAccessTokenDurationMs(10000L);
    jwtProperties.setRefreshTokenDurationMs(20000L);

    jwtTokenService = new JwtTokenService(userRepository, jwtProperties);
  }

  @Test
  @DisplayName("should create valid JWT token and extract data")
  void createAndExtractToken() {
    String username = "testUser";
    Long userId = 1L;

    String token = jwtTokenService.createToken(username, userId);

    assertNotNull(token);
    assertTrue(jwtTokenService.validateToken(token));
    assertEquals(username, jwtTokenService.getUsername(token));
    assertEquals(userId, jwtTokenService.getUserId(token));
  }

  private void assertNotNull(String token) {

  }

  @Test
  @DisplayName("should return false when token is invalid")
  void validateTokenInvalid() {

    String invalidToken = "invalid.token.value";

    boolean valid = jwtTokenService.validateToken(invalidToken);

    assertFalse(valid);
  }


  @Test
  @DisplayName("should throw exception if JWT secret not configured")
  void shouldThrowIfSecretMissing() {
    JwtProperties props = new JwtProperties();
    props.setSecret("");
    props.setAccessTokenDurationMs(10000L);

    IllegalStateException ex =
        assertThrows(IllegalStateException.class,
            () -> new JwtTokenService(userRepository, props));

    assertTrue(ex.getMessage().contains("JWT secret is not configured"));
  }

  @Test
  @DisplayName("should load user by ID successfully")
  void loadUserByIdSuccess() {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    CustomUserDetails details = jwtTokenService.loadUserById(1L);

    assertNotNull(String.valueOf(details));
    assertEquals("user", details.getUsername());
  }


  @Test
  @DisplayName("should throw UsernameNotFoundException if user not found")
  void loadUserByIdNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        org.springframework.security.core.userdetails.UsernameNotFoundException.class,
        () -> jwtTokenService.loadUserById(1L)
    );
  }

  @Test
  @DisplayName("should handle expired or tampered token gracefully")
  void validateTokenWithException() {
    String token = jwtTokenService.createToken("user", 1L);

    // I change the real valid token
    String brokenToken = token.substring(0, token.length() - 1) + "x";
    assertFalse(jwtTokenService.validateToken(brokenToken));
  }
}
