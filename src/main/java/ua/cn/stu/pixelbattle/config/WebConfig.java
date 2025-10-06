package ua.cn.stu.pixelbattle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class that customizes MVC settings.
 *
 * <p>Specifically, it configures Cross-Origin Resource Sharing (CORS)
 * to allow requests from all origins and standard HTTP methods.
 */
@Configuration
@Profile("prod")
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns("https://pixel-battle.zebaro.dev", "http://localhost:80")
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*")
        .allowCredentials(true);
  }
}
