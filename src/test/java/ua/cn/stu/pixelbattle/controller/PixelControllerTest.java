package ua.cn.stu.pixelbattle.controller;


import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.cn.stu.pixelbattle.dto.PixelChangeRequest;
import ua.cn.stu.pixelbattle.dto.PixelResponse;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;
import ua.cn.stu.pixelbattle.security.JwtAuthenticationFilter;
import ua.cn.stu.pixelbattle.service.PixelService;

/**
 * Tests PixelController endpoints for getting and changing pixels.
 */
@WebMvcTest(
    controllers = PixelController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = JwtAuthenticationFilter.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
public class PixelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PixelService pixelService;

  // -------------- GET ALL PIXEL -------------------
  @Test
  @DisplayName("GET /api/v1/pixel - returns list of all pixels")
  void getAllPixels_success() throws Exception {

    List<PixelResponse> mockPixels = List.of(
        new PixelResponse(0, 0, "#FFFFFF"),
        new PixelResponse(1, 1, "#000000")
    );
    when(pixelService.getAllPixels()).thenReturn(mockPixels);

    mockMvc.perform(get("/api/v1/pixel"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].coordinateX").value(0))
        .andExpect(jsonPath("$[0].color").value("#FFFFFF"))
        .andExpect(jsonPath("$[1].coordinateX").value(1));

    verify(pixelService, times(1)).getAllPixels();

  }

  @Test
  @DisplayName("GET /api/v1/pixel - returns empty list")
  void getAllPixels_empty() throws Exception {
    when(pixelService.getAllPixels()).thenReturn(List.of());

    mockMvc.perform(get("/api/v1/pixel"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }


  // ---------------POST CHANGE PIXEL ---------------------

  @Test
  @WithMockUser(username = "testUser", roles = "USER")
  @DisplayName("POST /api/v1/pixel/change - success when authenticated")
  void changePixel_success() throws Exception {
    PixelChangeRequest request = new PixelChangeRequest();
    request.setCoordinateX(10);
    request.setCoordinateY(20);
    request.setColor("#FF0000");

    CustomUserDetails userDetails = mock(CustomUserDetails.class);
    when(userDetails.getId()).thenReturn(1L);
    when(userDetails.getUsername()).thenReturn("testUser");

    SecurityContextHolder.getContext().setAuthentication(
        new TestingAuthenticationToken(userDetails, null, "ROLE_USER")
    );

    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pixel/change")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(pixelService, times(1))
        .changePixel(eq(10), eq(20), eq("#FF0000"), anyLong());
  }

  @Test
  @DisplayName("POST /api/v1/pixel/change - unauthorized user returns 401")
  void changePixel_unauthorized() throws Exception {
    PixelChangeRequest request = new PixelChangeRequest();
    request.setCoordinateX(10);
    request.setCoordinateY(20);
    request.setColor("#FF0000");


    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pixel/change")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(csrf()))
        .andExpect(status().isUnauthorized());

    verify(pixelService, never()).changePixel(anyInt(), anyInt(), anyString(), anyLong());
  }

}
