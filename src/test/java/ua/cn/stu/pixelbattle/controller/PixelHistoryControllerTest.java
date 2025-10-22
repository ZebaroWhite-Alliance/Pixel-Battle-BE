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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.cn.stu.pixelbattle.model.PixelHistory;
import ua.cn.stu.pixelbattle.security.JwtAuthenticationFilter;
import ua.cn.stu.pixelbattle.service.PixelHistoryService;


/**
 * Tests for PixelHistoryController.
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
  @DisplayName("GET /api/v1/history/after/{id} — success when valid id")
  void getAllAfter_success() throws Exception {
    PixelHistory pixel = new PixelHistory();
    pixel.setCoordinateX(1);
    pixel.setCoordinateY(2);
    pixel.setNewColor("#FF00FF");

    when(pixelHistoryService.getAllAfterId(10L)).thenReturn(List.of(pixel));

    mockMvc.perform(get("/api/v1/history/after/10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].coordinateX").value(1))
        .andExpect(jsonPath("$[0].coordinateY").value(2))
        .andExpect(jsonPath("$[0].color").value("#FF00FF"));
  }

  @Test
  @DisplayName("GET /api/v1/history/after/{id} — bad request when id is negative")
  void getAllAfter_negativeId() throws Exception {
    mockMvc.perform(get("/api/v1/history/after/-5"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /api/v1/history/after/{id}  empty result when id is too large")
  void getAllAfter_tooLargeId() throws Exception {
    when(pixelHistoryService.getAllAfterId(2000L)).thenReturn(List.of());

    mockMvc.perform(get("/api/v1/history/after/2000"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }
}
