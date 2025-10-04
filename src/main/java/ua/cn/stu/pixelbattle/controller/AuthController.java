package ua.cn.stu.pixelbattle.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * <p>Provides APIs for user registration, login, and refreshing JWT tokens.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  /**
   * Registers a new user and returns authentication tokens.
   *
   * @param req registration request containing username and password
   * @return AuthResponse containing access and refresh tokens
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
    authService.register(req);

    AuthRequest authRequest = new AuthRequest(req.getUsername(), req.getPassword());
    AuthResponse authResponse = authService.login(authRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
  }

  /**
   * Authenticates a user and returns JWT tokens.
   *
   * @param req login request containing username and password
   * @return AuthResponse containing access and refresh tokens
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
    AuthResponse authResponse = authService.login(req);
    return ResponseEntity.status(HttpStatus.OK).body(authResponse);
  }

  /**
   * Refreshes the access token using a valid refresh token.
   *
   * @param refreshTokenStr the refresh token string
   * @return AuthResponse containing a new access token and refresh token
   */
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshTokenStr) {
    AuthResponse authResponse = authService.refreshToken(refreshTokenStr);
    return ResponseEntity.ok(authResponse);
  }


}
