package ua.cn.stu.pixelbattle.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.cn.stu.pixelbattle.dto.AuthRequest;
import ua.cn.stu.pixelbattle.dto.AuthResponse;
import ua.cn.stu.pixelbattle.dto.RegisterRequest;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.security.JwtAuthenticationFilter;
import ua.cn.stu.pixelbattle.service.AuthService;


/**
 * Unit tests for {@link AuthController}.
 * Verifies authentication, registration, refresh and logout endpoints.
 */
@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = JwtAuthenticationFilter.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AuthService authService;


  // ------------------REGISTER----------------

  @Test
  @DisplayName("POST /auth/register success (201, sets cookie)")
  void register_success() throws Exception {
    when(authService.login(any(AuthRequest.class)))
        .thenReturn(new AuthResponse("access", "refresh"));

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                                {"username": "testuser", "password": "12345Acb*"}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value("access"))
        .andExpect(header().string("Set-Cookie",
            org.hamcrest.Matchers.containsString("refreshToken=")));
  }

  @Test
  @DisplayName("POST /auth/register username taken then 400 JSON error")
  void register_usernameTaken_returns400() throws Exception {
    doThrow(new IllegalArgumentException("Username already taken"))
        .when(authService).register(any(RegisterRequest.class));

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"username": "testuser", "password": "12345Acb*"}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Username already taken"))
        .andExpect(jsonPath("$.error").value("Bad Request"));
  }

  @Test
  @DisplayName("POST /auth/register return 400 when password is too weak")
  void register_whenPasswordWeak_returns400() throws Exception {
    String weakPasswordJson = """
        {
          "username": "testuser",
          "password": "abc"
        }
        """;

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(weakPasswordJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.fields.password")
            .value("Password must be at least 8 characters "
                + "long and include uppercase, lowercase letters, and digits"));
  }

  @Test
  @DisplayName("POST /auth/register  unexpected error and catch 500 JSON error")
  void register_unexpectedError_returns500() throws Exception {
    doThrow(new RuntimeException("Unexpected failure"))
        .when(authService).register(any(RegisterRequest.class));

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"username": "alex", "password": "12345Acb*"}
                """))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Internal Server Error"))
        .andExpect(jsonPath("$.message").value("Unexpected failure"));
  }

  // ------------------LONGIN------------------

  @Test
  @DisplayName("POST /auth/login success (coed 200, returns tokens and sets cookie)")
  void login_success() throws Exception {
    when(authService.login(any(AuthRequest.class)))
        .thenReturn(new AuthResponse("access-token", "refresh-token"));

    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"username": "testuser", "password": "12345Acb*"}
                """))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.token").value("access-token"))
        .andExpect(header().string("Set-Cookie",
            org.hamcrest.Matchers.containsString("refreshToken=")));
  }

  @Test
  @DisplayName("POST /auth/login - invalid password then 401 JSON error")
  void login_invalidPassword_returns401() throws Exception {
    doThrow(new org.springframework.security.authentication
        .BadCredentialsException("Invalid password"))
        .when(authService).login(any(AuthRequest.class));

    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"username": "testuser", "password": "wrongpass"}
                """))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Unauthorized"))
        .andExpect(jsonPath("$.message").value("Invalid password"));
  }

  @Test
  @DisplayName("POST /auth/login - user not found and 404 JSON error")
  void login_userNotFound_returns404() throws Exception {
    doThrow(new org.springframework.security.core.userdetails
        .UsernameNotFoundException("User not found"))
        .when(authService).login(any(AuthRequest.class));

    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"username": "testuser", "password": "123456Abc*"}
                """))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("User not found"));
  }


  // ------------------REFRESH------------------
  @Test
  @DisplayName("POST /auth/refresh - success, returns new access token and sets refresh cookie")
  void refreshToken_success() throws Exception {
    when(authService.refreshToken("oldRefresh"))
        .thenReturn(new AuthResponse("newAccess", "newRefresh"));

    mockMvc.perform(post("/api/v1/auth/refresh")
            .cookie(new Cookie("refreshToken", "oldRefresh")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("newAccess"))
        .andExpect(header().string("Set-Cookie",
            org.hamcrest.Matchers.containsString("refreshToken=newRefresh")));
  }

  @Test
  @DisplayName("POST /auth/refresh - missing or blank refresh token and 401 err")
  void refreshToken_missingToken_returns401() throws Exception {
    when(authService.refreshToken(null))
        .thenThrow(new ApiException("Missing refresh token", HttpStatus.UNAUTHORIZED));

    mockMvc.perform(post("/api/v1/auth/refresh"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Missing refresh token"));
  }

  // ------------------LOGOUT------------------
  @Test
  @DisplayName("POST /auth/logout - success, clears refresh cookie")
  void logout_success() throws Exception {
    mockMvc.perform(post("/api/v1/auth/logout")
            .cookie(new Cookie("refreshToken", "someToken")))
        .andExpect(status().isOk())
        .andExpect(header().string("Set-Cookie",
            org.hamcrest.Matchers.containsString("refreshToken=;")))
        .andExpect(header().string("Set-Cookie",
            org.hamcrest.Matchers.containsString("Max-Age=0")));

    verify(authService).logout("someToken");
  }

  @Test
  @DisplayName("POST /auth/logout - no cookie still returns 200")
  void logout_noCookie_returns200() throws Exception {
    mockMvc.perform(post("/api/v1/auth/logout"))
        .andExpect(status().isOk());

    verify(authService).logout(null);
  }
}
