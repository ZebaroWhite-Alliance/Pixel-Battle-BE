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
 * Unit tests for {@link SessionController}.
 *
 * <p>Verifies the /session endpoint behavior for valid sessions,
 * unauthorized access, and unexpected server errors.
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
  @DisplayName("should return 200 and session JSON when session is valid")
  void shouldReturn200AndSessionJsonWhenSessionIsValid() throws Exception {
    UserSessionResponse response = new UserSessionResponse(5L, "Hugo");
    when(sessionService.getSessionResponse(any(HttpServletRequest.class))).thenReturn(response);

    mockMvc.perform(get("/api/v1/session"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(5))
        .andExpect(jsonPath("$.username").value("Hugo"));
  }

  @Test
  @DisplayName("should return 401 when session is invalid or expired")
  void shouldReturn401WhenSessionIsInvalidOrExpired() throws Exception {
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
  @DisplayName("should return 500 when unexpected runtime exception occurs")
  void shouldReturn500WhenUnexpectedRuntimeExceptionOccurs() throws Exception {
    when(sessionService.getSessionResponse(any(HttpServletRequest.class)))
        .thenThrow(new RuntimeException("Unexpected failure"));

    mockMvc.perform(get("/api/v1/session"))
        .andExpect(status().isInternalServerError());
  }
}

