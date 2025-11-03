package ua.cn.stu.pixelbattle.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.cn.stu.pixelbattle.dto.GameInfoResponse;
import ua.cn.stu.pixelbattle.security.JwtAuthenticationFilter;
import ua.cn.stu.pixelbattle.service.PixelService;

/**
 * Unit tests for {@link GameInfoController}.
 *
 * <p>Verifies the /info endpoint for returning correct game information
 * as JSON response. Follows the style:
 * should[ExpectedBehavior]When[Condition].
 */
@WebMvcTest(GameInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GameInfoControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  private PixelService pixelService;

  @MockitoBean
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  @DisplayName("should return game info successfully when /info is called")
  void shouldReturnGameInfoSuccessfullyWhenEndpointCalled() throws Exception {


    GameInfoResponse response = new GameInfoResponse(1000, 1000, 1);
    when(pixelService.getGameInfo()).thenReturn(response);


    mockMvc.perform(get("/api/v1/info"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.width").value(1000))
        .andExpect(jsonPath("$.height").value(1000))
        .andExpect(jsonPath("$.cooldown").value(1));

    verify(pixelService).getGameInfo();

  }

}
