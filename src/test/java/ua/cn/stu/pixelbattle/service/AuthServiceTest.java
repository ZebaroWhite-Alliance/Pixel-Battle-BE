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
  @DisplayName("reg")
  void registerSuccess() {
    RegisterRequest registerRequest = new RegisterRequest("user", "123456Abc*");

    when(userRepository.existsByUsername("user")).thenReturn(false);
    when(passwordEncoder.encode("123456Abc*")).thenReturn("passHash");

    authService.register(registerRequest);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("registerUserExist")
  void registerUserExist() {
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
  @DisplayName("Successful login")
  void loginSuccess() {
    AuthRequest authRequest = new AuthRequest("user", "123456Abc*");
    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername("user");
    mockUser.setPasswordHash("encodedPass");

    when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches("123456Abc*", "encodedPass")).thenReturn(true);
    when(jwtTokenService.createToken("user", 1L)).thenReturn("access-token");
    when(refreshTokenService.createRefreshToken(1L)).thenReturn("refresh-token");

    AuthResponse response = authService.login(authRequest);

    assertEquals("access-token", response.getToken());
    assertEquals("refresh-token", response.getRefreshToken());

    verify(refreshTokenService).deleteByUserId(1L);

  }

  @Test
  @DisplayName("Login User not found")
  void loginUserNotFound() {
    AuthRequest authRequest = new AuthRequest("user", "123456Abc*");
    when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

    UsernameNotFoundException ex =  assertThrows(UsernameNotFoundException.class,
        () -> authService.login(authRequest));
    assertEquals("User not found", ex.getMessage());

  }

  @Test
  @DisplayName("login password is incorrect")
  void loginPasswordIncorrect() {
    AuthRequest authRequest = new AuthRequest("user", "123456Abc*");

    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername("user");
    mockUser.setPasswordHash("encodedPass");

    when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches("123456Abc*",
        "encodedPass")).thenReturn(false);


    BadCredentialsException ex = assertThrows(
        BadCredentialsException.class,
        () -> authService.login(authRequest)
    );

    assertEquals("Invalid password", ex.getMessage());
  }

  // -------------REFRESH-----------------

  @Test
  @DisplayName("successRefreshToken")
  void successRefreshToken() {
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
  @DisplayName("RefreshTokenEmpty")
  void RefreshTokenIsEmpty() {

    ApiException ex = assertThrows(ApiException.class,
        () -> authService.refreshToken(null));
    assertEquals("Missing refresh token", ex.getMessage());

  }

  @Test
  @DisplayName("refreshTokenUserNotFound")
  void userNotFound() {

    when(refreshTokenService.verifyExpiration("token")).thenReturn(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
        () -> authService.refreshToken("token"));

    assertEquals("User not found", ex.getMessage());
  }



  // ----------------------LOGOUT---------------------------------
  @Test
  @DisplayName("authLogoutSuccess")
  void authLogoutSuccess() {
    String refreshToken = "Refresh123";
    authService.logout(refreshToken);

    verify(refreshTokenService).deleteByToken(refreshToken);
  }


  @Test
  @DisplayName("authLogoutRefreshTokenNull")
  void authLogoutRefreshTokenNull() {
    authService.logout(null);
    authService.logout("");
    verify(refreshTokenService, never()).deleteByToken(any());
  }
}
