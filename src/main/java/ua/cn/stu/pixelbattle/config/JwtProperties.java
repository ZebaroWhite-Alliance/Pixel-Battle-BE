package ua.cn.stu.pixelbattle.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT tokens.
 * Stores the secret key and token expiration durations.
 * Values are injected from environment variables or default values.
 */
@Component
@Data
public class JwtProperties {

  /** Secret key used for signing JWT tokens. */
  @Value("${JWT_SECRET}")
  private String secret;

  /** Access token expiration time in milliseconds. */
  @Value("${jwt.access-token-duration-ms:300000}")
  private long accessTokenDurationMs;

  /** Refresh token expiration time in milliseconds. */
  @Value("${jwt.refresh-token-duration-ms:604800000}")
  private long  refreshTokenDurationMs;

  /**
   * Initialization method called after bean creation.
   * Prints the JWT secret to the console for debugging purposes.
   */
  @PostConstruct
  public void init() {
    System.out.println("JWT secret = " + secret);
  }
}
