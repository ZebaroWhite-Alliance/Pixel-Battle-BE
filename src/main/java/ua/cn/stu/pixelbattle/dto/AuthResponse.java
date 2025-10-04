package ua.cn.stu.pixelbattle.dto;

import lombok.Getter;
import lombok.Setter;


/**
 * Data Transfer Object (DTO) representing the response
 * returned after a successful authentication attempt.
 *
 * <p>Contains both the short-lived access token and,
 * optionally, a long-lived refresh token for session renewal.</p>
 */
@Getter
@Setter
public class AuthResponse {
  // JWT access token
  private String token;

  // used to obtain a new access token without requiring the user to log in again
  private String refreshToken;

  /**
   * Creates an AuthResponse with only an access token.
   *
   * @param token the generated access token
   */
  public AuthResponse(String token) {
    this.token = token;
  }

  /**
   * Creates an AuthResponse with both access and refresh tokens.
   *
   * @param token        the generated access token
   * @param refreshToken the generated refresh token
   */
  public AuthResponse(String token, String refreshToken) {
    this.token = token;
    this.refreshToken = refreshToken;
  }
}
