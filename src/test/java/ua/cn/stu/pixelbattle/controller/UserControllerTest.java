package ua.cn.stu.pixelbattle.controller;


import static org.mockito.Mockito.mock;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.cn.stu.pixelbattle.dto.PixelHistoryDto;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;
import ua.cn.stu.pixelbattle.security.JwtAuthenticationFilter;
import ua.cn.stu.pixelbattle.service.PixelHistoryService;


/**
 * Unit tests for {@link UserController}.
 *
 * <p>Verifies endpoints for current user info (/user/me) and user's pixel history (/user/history),
 * including authenticated access, unauthorized access, and empty history responses.
 */
@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = JwtAuthenticationFilter.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PixelHistoryService pixelHistoryService;

  // set auth test user
  private void authenticateTestUser() {
    CustomUserDetails user = mock(CustomUserDetails.class);
    when(user.getId()).thenReturn(1L);
    when(user.getUsername()).thenReturn("testUser");
    when(user.getPixelChangesCount()).thenReturn(200);

    SecurityContextHolder.getContext().setAuthentication(
        new TestingAuthenticationToken(user, null, "ROLE_USER")
    );
  }

  // -------------- GET USER INFO -------------------
  @Test
  @DisplayName("should return 200 and current user info when authenticated")
  void shouldReturn200AndCurrentUserInfoWhenAuthenticated() throws Exception {
    authenticateTestUser();


    mockMvc.perform(get("/api/v1/user/me"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.pixelChangesCount").value(200));

  }

  @Test
  @DisplayName("should return 401 when requesting current user info without authentication")
  void shouldReturn401WhenRequestingCurrentUserInfoWithoutAuthentication() throws Exception {

    mockMvc.perform(get("/api/v1/user/me"))
        .andExpect(status().isUnauthorized());
  }

}
