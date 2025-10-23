package ua.cn.stu.pixelbattle.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.dto.UserSessionResponse;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.model.User;

/**
 * Service for handling user session logic.
 *
 * <p>Responsible for validating HTTP-only refresh tokens from cookies
 * and retrieving minimal user information for the frontend.
 */
@Service
@AllArgsConstructor
public class SessionService {

  private final RefreshTokenService refreshTokenService;
  private final UserService userService;

  /**
   * Checks the current user's session based on the refresh token cookie
   * and returns a minimal {@link UserSessionResponse} if the session is valid.
   *
   * <p>This method performs the following steps:
   * <ol>
   *   <li>Retrieves cookies from the {@link HttpServletRequest}.</li>
   *   <li>Extracts the "refreshToken" cookie.</li>
   *   <li>Validates the refresh token using {@link RefreshTokenService}.</li>
   *   <li>Fetches the corresponding {@link User} from the database.</li>
   *   <li>If valid, returns a {@link UserSessionResponse} containing
   *       the user's id and username.</li>
   *   <li>If any step fails, returns {@code null}.</li>
   * </ol>
   *
   * @param request the {@link HttpServletRequest} containing cookies
   * @return a {@link UserSessionResponse} if the session is valid; {@code null} otherwise
   */
  public UserSessionResponse getSessionResponse(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      throw new ApiException("No cookies found", HttpStatus.UNAUTHORIZED);
    }

    String refreshToken = extractRefreshToken(cookies);
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new ApiException("Missing refresh token", HttpStatus.UNAUTHORIZED);
    }

    Long userId = refreshTokenService.verifyExpiration(refreshToken);
    if (userId == null) {
      throw new ApiException("Refresh token invalid or expired", HttpStatus.UNAUTHORIZED);
    }

    User user = userService.getUserById(userId)
        .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));

    return new UserSessionResponse(user.getId(), user.getUsername());
  }


  private String extractRefreshToken(Cookie[] cookies) {
    for (Cookie cookie : cookies) {
      if ("refreshToken".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }
}
