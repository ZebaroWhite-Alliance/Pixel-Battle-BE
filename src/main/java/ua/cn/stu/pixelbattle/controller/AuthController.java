package ua.cn.stu.pixelbattle.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixelbattle.dto.AuthRequest;
import ua.cn.stu.pixelbattle.dto.AuthResponse;
import ua.cn.stu.pixelbattle.dto.RegisterRequest;
import ua.cn.stu.pixelbattle.service.AuthService;

/**
 * Controller for authentication endpoints.
 *
 * <p>Provides APIs for user registration, login,  refreshing and logut JWT tokens.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private static final String REFRESH_COOKIE_NAME = "refreshToken";
  private static final Duration REFRESH_TOKEN_AGE = Duration.ofDays(7);

  /**
   * Registers a new user and immediately logs them in.
   *
   * <p>The access token is returned in the response body, and the refresh token
   * is stored as an HTTP-only cookie.</p>
   *
   * @param req the registration request containing {@code username} and {@code password}
   * @param response the {@link HttpServletResponse} used to add the refresh token cookie
   * @return a {@link ResponseEntity} containing the access token in {@link AuthResponse}
   *         and HTTP status 201 CREATED
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req,
                                    HttpServletResponse response) {
    authService.register(req);

    AuthRequest authRequest = new AuthRequest(req.getUsername(), req.getPassword());
    AuthResponse authResponse = authService.login(authRequest);

    addRefreshCookie(response, authResponse.getRefreshToken());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new AuthResponse(authResponse.getToken()));
  }

  /**
   * Authenticates an existing user.
   *
   * <p>The access token is returned in the response body, and the refresh token
   * is stored as an HTTP-only cookie.</p>
   *
   * @param req the login request containing {@code username} and {@code password}
   * @param response the {@link HttpServletResponse} used to add the refresh token cookie
   * @return a {@link ResponseEntity} containing the access token in {@link AuthResponse}
   *         and HTTP status 200 OK
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req,
                                            HttpServletResponse response) {
    AuthResponse authResponse = authService.login(req);
    addRefreshCookie(response, authResponse.getRefreshToken());

    return ResponseEntity.ok(new AuthResponse(authResponse.getToken()));
  }

  /**
   * Refreshes the access token using the refresh token stored in an HTTP-only cookie.
   *
   * <p>If the refresh token is missing or invalid, returns HTTP status 401 Unauthorized.
   * Otherwise, a new access token is returned in the response body and the refresh
   * token is updated in the cookie.</p>
   *
   * @param refreshToken the value of the refresh token cookie (optional)
   * @param response the {@link HttpServletResponse} used to update the refresh token cookie
   * @return a {@link ResponseEntity} containing the new access token in {@link AuthResponse}
   *         or HTTP status 401 if unauthorized
   */
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(
      @CookieValue(value = REFRESH_COOKIE_NAME, required = false) String refreshToken,
                                                   HttpServletResponse response) {

    AuthResponse authResponse = authService.refreshToken(refreshToken);
    addRefreshCookie(response, authResponse.getRefreshToken());
    return ResponseEntity.ok(new AuthResponse(authResponse.getToken()));
  }

  /**
   * Logs out the current user by deleting the refresh token.
   *
   * <p>The refresh token cookie is cleared on the client, effectively ending the session.</p>
   *
   * @param refreshToken the value of the refresh token cookie (optional)
   * @param response the {@link HttpServletResponse} used to clear the cookie
   * @return a {@link ResponseEntity} with HTTP status 200 OK
   */
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@CookieValue(value = REFRESH_COOKIE_NAME, required = false)
                                       String refreshToken,
                                     HttpServletResponse response) {
    authService.logout(refreshToken);

    ResponseCookie deleteCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .sameSite("Lax")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    return ResponseEntity.ok().build();
  }

  /**
   * Adds or updates the HTTP-only refresh token cookie for the client.
   *
   * <p>The cookie is used to refresh access tokens without requiring
   * the user to log in again.</p>
   *
   * @param response the {@link HttpServletResponse} used to add the cookie
   * @param refreshToken the refresh token value
   */
  private void addRefreshCookie(HttpServletResponse response, String refreshToken) {
    ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
        .httpOnly(true)
        .secure(true) // in prod must be true?
        .path("/")
        .maxAge(REFRESH_TOKEN_AGE)
        .sameSite("Lax")
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
