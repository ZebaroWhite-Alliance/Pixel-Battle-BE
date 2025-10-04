package ua.cn.stu.pixelbattle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the PixelBattle Spring Boot application.
 *
 * <p>Bootstraps the Spring context and starts the embedded web server.
 */
@SpringBootApplication
public class PixelBattleApplication {

  /**
   * Launches the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(PixelBattleApplication.class, args);
  }

}
