package ua.cn.stu.pixelbattle.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.dto.AuthRequest;
import ua.cn.stu.pixelbattle.dto.AuthResponse;
import ua.cn.stu.pixelbattle.dto.RegisterRequest;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.UserRepository;


/**
 * Service responsible for user authentication, registration,
 * and JWT/refresh token management.
 *
 * <p>Provides functionality for:
 * <ul>
 *   <li>User registration with password hashing</li>
 *   <li>Login with username/password validation</li>
 *   <li>Token generation (access & refresh)</li>
 *   <li>Token renewal using refresh tokens</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService jwtTokenService;
  private final RefreshTokenService refreshTokenService;

  /**
   * Registers a new user in the system.
   *
   * @param req the registration request containing username and password
   * @throws IllegalArgumentException if the username is already taken
   */
  public void register(RegisterRequest req) {
    if (userRepo.existsByUsername(req.getUsername())) {
      throw new IllegalArgumentException("Username already taken");
    }
    User user = new User();
    user.setUsername(req.getUsername());
    user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    userRepo.save(user);
  }

  /**
   * Authenticates a user and generates new access & refresh tokens.
   *
   * @param req the authentication request containing username and password
   * @return an {@link AuthResponse} containing the generated tokens
   * @throws UsernameNotFoundException if the user does not exist
   * @throws BadCredentialsException   if the provided password is invalid
   */
  @Transactional
  public AuthResponse login(AuthRequest req) {
    User user = userRepo.findByUsername(req.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
      throw new BadCredentialsException("Invalid password");
    }


    refreshTokenService.deleteByUserId(user.getId());

    String accessToken = jwtTokenService.createToken(user.getUsername(), user.getId());
    String refreshToken = refreshTokenService.createRefreshToken(user.getId());

    return new AuthResponse(accessToken, refreshToken);
  }

  /**
   * Refreshes the authentication tokens using a valid refresh token.
   *
   * @param refreshTokenStr the refresh token string
   * @return an {@link AuthResponse} containing new access and refresh tokens
   * @throws RuntimeException if the refresh token is invalid or user not found
   */
  public AuthResponse refreshToken(String refreshTokenStr) {
    Long userId = refreshTokenService.verifyExpiration(refreshTokenStr);
    refreshTokenService.deleteByToken(refreshTokenStr);

    User user = userRepo.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    String newAccessToken = jwtTokenService.createToken(user.getUsername(), user.getId());
    String newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

    return new AuthResponse(newAccessToken, newRefreshToken);
  }
}
