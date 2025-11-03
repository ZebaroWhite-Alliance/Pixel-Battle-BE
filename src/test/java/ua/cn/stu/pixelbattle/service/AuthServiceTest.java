package ua.cn.stu.pixelbattle.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.cn.stu.pixelbattle.dto.AuthRequest;
import ua.cn.stu.pixelbattle.dto.AuthResponse;
import ua.cn.stu.pixelbattle.dto.RegisterRequest;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.UserRepository;

/**
 * Unit tests for {@link AuthService}.
 *
 * <p>Verifies main authentication scenarios:
 * <ul>
 *   <li>User registration and duplicate username handling</li>
 *   <li>Login success and invalid credentials</li>
 *   <li>Token refresh flow and error cases</li>
 *   <li>Logout behavior</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
  @Mock
  UserRepository userRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  JwtTokenService jwtTokenService;

  @Mock
  RefreshTokenService refreshTokenService;

  @InjectMocks
  AuthService authService;

  // ----------REGISTER-----------------
  @Test
  @DisplayName("should register new user successfully")
  void shouldRegisterNewUserSuccessfully() {
    RegisterRequest registerRequest = new RegisterRequest("user", "123456Abc*");

    when(userRepository.existsByUsername("user")).thenReturn(false);
    when(passwordEncoder.encode("123456Abc*")).thenReturn("passHash");

    authService.register(registerRequest);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("should throw when username already exists")
  void shouldThrowWhenUsernameAlreadyExists() {
    RegisterRequest registerRequest = new RegisterRequest(
        "userExist", "123456Abc*");

    when(userRepository.existsByUsername("userExist")).thenReturn(true);
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> authService.register(registerRequest));

    assertEquals("Username already taken", ex.getMessage());

    verify(userRepository, never()).save(any());

  }

  // -------------------LOGIN--------------------------

  @Test
  @DisplayName("should login successfully with correct credentials")
  void shouldLoginSuccessfullyWithCorrectCredentials() {
    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername("user");
    mockUser.setPasswordHash("encodedPass");

    when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches("123456Abc*", "encodedPass")).thenReturn(true);
    when(jwtTokenService.createToken("user", 1L)).thenReturn("access-token");
    when(refreshTokenService.createRefreshToken(1L)).thenReturn("refresh-token");

    AuthRequest authRequest = new AuthRequest("user", "123456Abc*");
    AuthResponse response = authService.login(authRequest);

    assertEquals("access-token", response.getToken());
    assertEquals("refresh-token", response.getRefreshToken());

    verify(refreshTokenService).deleteByUserId(1L);

  }

  @Test
  @DisplayName("should throw when user not found")
  void shouldThrowWhenUserNotFoundOnLogin() {
    when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

    AuthRequest authRequest = new AuthRequest("user", "123456Abc*");
    UsernameNotFoundException ex =  assertThrows(UsernameNotFoundException.class,
        () -> authService.login(authRequest));
    assertEquals("User not found", ex.getMessage());

  }

  @Test
  @DisplayName("should throw when password is incorrect")
  void shouldThrowWhenPasswordIsIncorrect() {

    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername("user");
    mockUser.setPasswordHash("encodedPass");

    when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches("123456Abc*",
        "encodedPass")).thenReturn(false);

    AuthRequest authRequest = new AuthRequest("user", "123456Abc*");
    BadCredentialsException ex = assertThrows(
        BadCredentialsException.class,
        () -> authService.login(authRequest)
    );

    assertEquals("Invalid password", ex.getMessage());
  }

  // -------------REFRESH-----------------

  @Test
  @DisplayName("should refresh tokens successfully")
  void shouldRefreshTokensSuccessfully() {
    String oldRefreshToken = "oldRefresh123";

    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername("user");


    when(refreshTokenService.verifyExpiration(oldRefreshToken)).thenReturn(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    when(jwtTokenService.createToken("user", 1L)).thenReturn("newAccessToken");
    when(refreshTokenService.createRefreshToken(1L)).thenReturn("newRefreshToken");

    AuthResponse response = authService.refreshToken(oldRefreshToken);

    assertNotNull(response);
    assertEquals("newAccessToken", response.getToken());
    assertEquals("newRefreshToken", response.getRefreshToken());


    verify(refreshTokenService).verifyExpiration(oldRefreshToken);
    verify(refreshTokenService).deleteByToken(oldRefreshToken);
    verify(userRepository).findById(1L);
  }


  @Test
  @DisplayName("should throw when refresh token is missing")
  void shouldThrowWhenRefreshTokenIsMissing() {

    ApiException ex = assertThrows(ApiException.class,
        () -> authService.refreshToken(null));
    assertEquals("Missing refresh token", ex.getMessage());

  }

  @Test
  @DisplayName("should throw when user not found on refresh")
  void shouldThrowWhenUserNotFoundOnRefresh() {

    when(refreshTokenService.verifyExpiration("token")).thenReturn(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
        () -> authService.refreshToken("token"));

    assertEquals("User not found", ex.getMessage());
  }



  // ----------------------LOGOUT---------------------------------
  @Test
  @DisplayName("should logout successfully")
  void shouldLogoutSuccessfully() {
    String refreshToken = "Refresh123";
    authService.logout(refreshToken);

    verify(refreshTokenService).deleteByToken(refreshToken);
  }


  @Test
  @DisplayName("should skip logout when token is null or empty")
  void shouldSkipLogoutWhenTokenIsNullOrEmpty() {
    authService.logout(null);
    authService.logout("");
    verify(refreshTokenService, never()).deleteByToken(any());
  }
}
