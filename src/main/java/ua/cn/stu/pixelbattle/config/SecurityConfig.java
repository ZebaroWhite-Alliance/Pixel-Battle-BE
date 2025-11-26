package ua.cn.stu.pixelbattle.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.cn.stu.pixelbattle.security.JwtAuthenticationFilter;
import ua.cn.stu.pixelbattle.service.JwtTokenService;

/**
 * Spring Security configuration class.
 *
 * <p>Defines security filter chain, password encoder, and authentication manager beans.
 * Configures public and protected endpoints, including Swagger, WebSocket, and REST APIs.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private static final String[] PUBLIC_URLS = {
      "/swagger-ui/**",
      "/api/v1/auth/register",
      "/api/v1/session",
      "/api/v1/auth/login",
      "/api/v1/auth/refresh",
      "/actuator/health",
      "/api/v1/pixels",
      "/api/v1/history",
      "/api/v1/info",
      "/ws/**",
      "/topic/**",
      "/app/**",
  };

  private static final String[] PROTECTED_URLS = {
      "/api/v1/current-user",
      "/api/v1/history/**",
  };

  /**
   * Constructs a SecurityConfig instance with required JWT services.
   *
   * @param jwtAuthenticationFilter the filter that validates JWTs in incoming requests
   */
  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  /**
   * Configures the security filter chain for the application.
   *
   * <p>Disables CSRF, sets public endpoints, protects sensitive endpoints,
   * and adds JWT authentication filter before UsernamePasswordAuthenticationFilter.
   *
   * @param http the HttpSecurity to configure
   * @return the built SecurityFilterChain
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(PUBLIC_URLS).permitAll()
            .requestMatchers(PROTECTED_URLS).authenticated()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((req, res, authEx) -> {
              res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              res.setContentType("application/json");
              res.getWriter().write(
                  "{\"error\":\"Unauthorized\",\"message\":\"Access token missing or invalid\"}"
              );
            })
        );
    return http.build();
  }

  /**
   * Creates a {@link PasswordEncoder} bean for password hashing.
   *
   * @return a BCryptPasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  /**
   * Exposes the {@link AuthenticationManager} bean.
   *
   * @param authConfig the AuthenticationConfiguration
   * @return the AuthenticationManager
   * @throws Exception if authentication manager cannot be created
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }
}