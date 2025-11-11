package ua.cn.stu.pixelbattle.controller;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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
import ua.cn.stu.pixelbattle.dto.PixelHistoryDto;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.model.PixelHistory;
import ua.cn.stu.pixelbattle.security.JwtAuthenticationFilter;
import ua.cn.stu.pixelbattle.service.PixelHistoryService;


/**
 * Unit tests for {@link PixelHistoryController}.
 *
 * <p>Verifies the /history/after/{id} endpoint for valid, invalid, and edge case requests.
 */
@WebMvcTest(
    controllers = PixelHistoryController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = JwtAuthenticationFilter.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
public class PixelHistoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PixelHistoryService pixelHistoryService;

  // ---------- getAllAfter ----------
  @Test
  @DisplayName("should return pixel history successfully when valid id is provided")
  void shouldReturnPixelHistorySuccessfullyWhenValidId() throws Exception {
    PixelHistoryDto dto = new PixelHistoryDto(1L, 1, 2, "#FF00FF");

    when(pixelHistoryService.getAllAfter(10L, 10000)).thenReturn(List.of(dto));

    mockMvc.perform(get("/api/v1/history")
            .param("fromId", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].x").value(1))
        .andExpect(jsonPath("$[0].y").value(2))
        .andExpect(jsonPath("$[0].new_color").value("#FF00FF"));
  }


  @Test
  @DisplayName("should return 400 Bad Request when fromId is negative")
  void shouldReturn400WhenFromIdIsNegative() throws Exception {
    when(pixelHistoryService.getAllAfter(-5L, 10000))
        .thenThrow(new ApiException("Invalid parameters", HttpStatus.BAD_REQUEST));

    mockMvc.perform(get("/api/v1/history")
            .param("fromId", "-5"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should return empty list when fromId is larger than any existing record")
  void shouldReturnEmptyListWhenIdIsTooLarge() throws Exception {
    when(pixelHistoryService.getAllAfter(2000L, 10000)).thenReturn(List.of());

    mockMvc.perform(get("/api/v1/history")
            .param("fromId", "2000"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  // ---------------------GET USER HISTORY------------------------------

  @Test
  @DisplayName("should return user pixel history successfully when valid params are provided")
  void shouldReturnUserPixelHistorySuccessfully() throws Exception {
    PixelHistoryDto dto = new PixelHistoryDto(1L, 5, 6, "#00FF00");

    when(pixelHistoryService.getUserHistory(null, null, 10L, 1000))
        .thenReturn(List.of(dto));

    mockMvc.perform(get("/api/v1/history/user")
            .param("fromId", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].x").value(5))
        .andExpect(jsonPath("$[0].y").value(6))
        .andExpect(jsonPath("$[0].new_color").value("#00FF00"));
  }

  @Test
  @DisplayName("should return 401 when current user is not authenticated")
  void shouldReturn401WhenUserNotAuthenticated() throws Exception {
    when(pixelHistoryService.getUserHistory(null, null, 10L, 1000))
        .thenThrow(new ApiException("Unauthorized", HttpStatus.UNAUTHORIZED));

    mockMvc.perform(get("/api/v1/history/user")
            .param("fromId", "10"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("should return 403 when non-admin user tries to access another user's history")
  void shouldReturn403WhenUserAccessesOthersHistory() throws Exception {
    when(pixelHistoryService.getUserHistory(null, 999L, 10L, 1000))
        .thenThrow(new ApiException("Access denied", HttpStatus.FORBIDDEN));

    mockMvc.perform(get("/api/v1/history/user")
            .param("fromId", "10")
            .param("userId", "999"))
        .andExpect(status().isForbidden());
  }

}
