package ua.cn.stu.pixel_battle.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtProperties {

  @Value("${JWT_SECRET}")
  private String secret;

  @Value("${jwt.access-token-duration-ms:300000}")
  private long accessTokenDurationMs;

  @Value("${jwt.refresh-token-duration-ms:604800000}")
  private long refreshTokenDurationMs;

  @PostConstruct
  public void init() {
    System.out.println("JWT secret = " + secret);
  }
}
