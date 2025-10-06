package ua.cn.stu.pixelbattle.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Game configuration properties (field size, cooldown).
 */
@Data
@Component
@ConfigurationProperties(prefix = "game")
public class GameProperties {
  private int width;
  private int height;
  private int cooldown;
}
