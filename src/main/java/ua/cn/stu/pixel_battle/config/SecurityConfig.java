package ua.cn.stu.pixel_battle.config;


import org.springframework.http.HttpMethod;
import ua.cn.stu.pixel_battle.security.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.cn.stu.pixel_battle.service.JWTTokenService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JWTTokenService jwtTokenService;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] PUBLIC_URLS = {
            "/pixel-battle/api/v1/auth/register",
            "/pixel-battle/api/v1/auth/login",
            "/pixel-battle/api/v1/actuator/health",
            "/pixel-battle/api/v1/pixel",
            "/pixel-battle/api/v1/pixel/**"
    };

    private static final String[] PROTECTED_URLS = {
            "/pixel-battle/api/v1/pixel/change",
            "/pixel-battle/api/v1/current-user"
    };
    public SecurityConfig(JWTTokenService jwtTokenService,
                          JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtTokenService = jwtTokenService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(PROTECTED_URLS).authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}