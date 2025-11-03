package ua.cn.stu.pixelbattle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ua.cn.stu.pixelbattle.dto.UserSessionResponse;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.model.User;

/**
 * Unit tests for {@link SessionService}.
 *
 * <p>Verifies main session-related scenarios:
 * <ul>
 *   <li>Valid token handling and response creation</li>
 *   <li>Error handling when cookies or tokens are missing</li>
 *   <li>Handling expired or invalid tokens</li>
 *   <li>Handling user-not-found scenarios</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private UserService userService;

  @Mock
  private HttpServletRequest request;

  @InjectMocks
  private SessionService sessionService;

  @Test
  @DisplayName("should return UserSessionResponse when token is valid")
  void shouldReturnUserSessionResponseWhenTokenIsValid() {
    Cookie[] cookies = { new Cookie("refreshToken", "abc123") };
    when(request.getCookies()).thenReturn(cookies);
    when(refreshTokenService.verifyExpiration("abc123")).thenReturn(1L);

    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    when(userService.getUserById(1L)).thenReturn(Optional.of(user));

    UserSessionResponse response = sessionService.getSessionResponse(request);

    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals("testUser", response.getUsername());
    verify(refreshTokenService).verifyExpiration("abc123");
    verify(userService).getUserById(1L);
  }

  @Test
  @DisplayName("should throw ApiException when no cookies found")
  void shouldThrowWhenNoCookiesFound() {
    when(request.getCookies()).thenReturn(null);

    ApiException ex = assertThrows(ApiException.class,
        () -> sessionService.getSessionResponse(request));

    assertEquals("No cookies found", ex.getMessage());
    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
  }

  @Test
  @DisplayName("should throw ApiException when refresh token is missing")
  void shouldThrowWhenRefreshTokenIsMissing() {
    Cookie[] cookies = { new Cookie("otherCookie", "123") };
    when(request.getCookies()).thenReturn(cookies);

    ApiException ex = assertThrows(ApiException.class,
        () -> sessionService.getSessionResponse(request));

    assertEquals("Missing refresh token", ex.getMessage());
  }

  @Test
  @DisplayName("should throw ApiException when token is invalid or expired")
  void shouldThrowWhenTokenIsInvalidOrExpired() {
    Cookie[] cookies = { new Cookie("refreshToken", "expiredToken") };
    when(request.getCookies()).thenReturn(cookies);
    when(refreshTokenService.verifyExpiration("expiredToken")).thenReturn(null);

    ApiException ex = assertThrows(ApiException.class,
        () -> sessionService.getSessionResponse(request));

    assertEquals("Refresh token invalid or expired", ex.getMessage());
  }

  @Test
  @DisplayName("should throw ApiException when user not found")
  void shouldThrowWhenUserNotFoundOnSession() {
    Cookie[] cookies = { new Cookie("refreshToken", "abc123") };
    when(request.getCookies()).thenReturn(cookies);
    when(refreshTokenService.verifyExpiration("abc123")).thenReturn(42L);
    when(userService.getUserById(42L)).thenReturn(Optional.empty());

    ApiException ex = assertThrows(ApiException.class,
        () -> sessionService.getSessionResponse(request));

    assertEquals("User not found", ex.getMessage());
  }
}
