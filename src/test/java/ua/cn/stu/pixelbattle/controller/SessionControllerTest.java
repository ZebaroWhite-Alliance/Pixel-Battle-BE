package ua.cn.stu.pixelbattle.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.cn.stu.pixelbattle.dto.UserSessionResponse;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.security.JwtAuthenticationFilter;
import ua.cn.stu.pixelbattle.service.SessionService;

/**
 * Tests for SessionController.
 * Checks the /session endpoint for different scenarios.
 */
@WebMvcTest(
    controllers = SessionController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = JwtAuthenticationFilter.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
public class SessionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SessionService sessionService;

  @Test
  @DisplayName("GET /session — return 200 and JSON when the session is valid")
  void getSession_whenValidSession_returns200AndJson() throws Exception {
    UserSessionResponse response = new UserSessionResponse(5L, "Hugo");
    when(sessionService.getSessionResponse(any(HttpServletRequest.class))).thenReturn(response);

    mockMvc.perform(get("/api/v1/session"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(5))
        .andExpect(jsonPath("$.username").value("Hugo"));
  }

  @Test
  @DisplayName("GET /session — return 401 when service throws ApiException")
  void getSession_whenServiceThrowsApiException_returns401() throws Exception {
    when(sessionService.getSessionResponse(any(HttpServletRequest.class)))
        .thenThrow(new ApiException("Invalid or expired token",
            org.springframework.http.HttpStatus.UNAUTHORIZED));

    mockMvc.perform(get("/api/v1/session"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Unauthorized"))
        .andExpect(jsonPath("$.message").value("Invalid or expired token"));
  }

  @Test
  @DisplayName("GET /session — return 500 when service throws generic RuntimeException")
  void getSession_whenServiceThrowsRuntimeException_returns500() throws Exception {
    when(sessionService.getSessionResponse(any(HttpServletRequest.class)))
        .thenThrow(new RuntimeException("Unexpected failure"));

    mockMvc.perform(get("/api/v1/session"))
        .andExpect(status().isInternalServerError());
  }
}

