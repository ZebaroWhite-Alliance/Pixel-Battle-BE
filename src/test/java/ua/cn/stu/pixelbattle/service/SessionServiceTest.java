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
 * Unit tests for {@link SessionService} using Mockito.
 * Tests user session validation logic and edge cases.
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
  @DisplayName("Returns UserSessionResponse when the token is valid")
  void getSessionResponseReturnsUserSessionWhenTokenValid() {
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
  @DisplayName("Throws an ApiException if there are no cookies")
  void getSessionResponseThrowsWhenNoCookies() {
    when(request.getCookies()).thenReturn(null);

    ApiException ex = assertThrows(ApiException.class,
        () -> sessionService.getSessionResponse(request));

    assertEquals("No cookies found", ex.getMessage());
    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
  }

  @Test
  @DisplayName("Throws an ApiException if refreshToken is missing")
  void getSessionResponseThrowsWhenMissingRefreshToken() {
    Cookie[] cookies = { new Cookie("otherCookie", "123") };
    when(request.getCookies()).thenReturn(cookies);

    ApiException ex = assertThrows(ApiException.class,
        () -> sessionService.getSessionResponse(request));

    assertEquals("Missing refresh token", ex.getMessage());
  }

  @Test
  @DisplayName("Throws an ApiException if the token is invalid or has expired.")
  void getSessionResponseThrowsWhenTokenInvalid() {
    Cookie[] cookies = { new Cookie("refreshToken", "expiredToken") };
    when(request.getCookies()).thenReturn(cookies);
    when(refreshTokenService.verifyExpiration("expiredToken")).thenReturn(null);

    ApiException ex = assertThrows(ApiException.class,
        () -> sessionService.getSessionResponse(request));

    assertEquals("Refresh token invalid or expired", ex.getMessage());
  }

  @Test
  @DisplayName("Throws an ApiException if the user is not found.")
  void getSessionResponseThrowsWhenUserNotFound() {
    Cookie[] cookies = { new Cookie("refreshToken", "abc123") };
    when(request.getCookies()).thenReturn(cookies);
    when(refreshTokenService.verifyExpiration("abc123")).thenReturn(42L);
    when(userService.getUserById(42L)).thenReturn(Optional.empty());

    ApiException ex = assertThrows(ApiException.class,
        () -> sessionService.getSessionResponse(request));

    assertEquals("User not found", ex.getMessage());
  }
}
