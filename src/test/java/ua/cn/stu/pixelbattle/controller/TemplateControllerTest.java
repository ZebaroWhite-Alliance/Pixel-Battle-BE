package ua.cn.stu.pixelbattle.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ua.cn.stu.pixelbattle.dto.CreateTemplateRequest;
import ua.cn.stu.pixelbattle.dto.TemplateResponse;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;
import ua.cn.stu.pixelbattle.security.JwtAuthenticationFilter;
import ua.cn.stu.pixelbattle.service.TemplateService;


/**
 * Unit tests for TemplateController covering template management endpoints.
 * Tests authentication, authorization, and CRUD operations with mocked service layer.
 */
@WebMvcTest(
    controllers = TemplateController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = JwtAuthenticationFilter.class)
    }
)
@AutoConfigureMockMvc(addFilters = false)
public class TemplateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private TemplateService templateService;

  private CustomUserDetails userDetails;

  @BeforeEach
  void setUp() {
    userDetails = mock(CustomUserDetails.class);
    when(userDetails.getId()).thenReturn(1L);
    when(userDetails.getRole()).thenReturn("USER");

    SecurityContextHolder.getContext().setAuthentication(
        new TestingAuthenticationToken(userDetails, null, "ROLE_USER")
    );
  }

  // --------------- CREATE TEMPLATE ----------------------

  @Test
  @DisplayName("should create template successfully when authenticated")
  void shouldCreateTemplateSuccessfullyWhenAuthenticated() throws Exception {
    CreateTemplateRequest request = new CreateTemplateRequest("My Template", List.of());
    TemplateResponse saved = new TemplateResponse(1L, "My Template", 1L, List.of());

    when(templateService.createTemplate(any(CreateTemplateRequest.class), any())).thenReturn(saved);

    mockMvc.perform(post("/api/v1/templates")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("My Template"))
        .andExpect(jsonPath("$.user_id").value(1));

    verify(templateService, times(1)).createTemplate(any(CreateTemplateRequest.class), any());
  }

  @Test
  @DisplayName("should return 401 unauthorized when creating template without authentication")
  void shouldReturn401WhenCreatingTemplateWithoutAuthentication() throws Exception {
    CreateTemplateRequest request = new CreateTemplateRequest("Unauthorized Template", List.of());

    SecurityContextHolder.clearContext();

    mockMvc.perform(post("/api/v1/templates")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());

    verify(templateService, never()).createTemplate(any(CreateTemplateRequest.class), any());
  }

  // --------------- GET MY TEMPLATES ----------------------

  @Test
  @DisplayName("should return all templates of the authenticated user")
  void shouldReturnAllTemplatesOfAuthenticatedUser() throws Exception {
    List<TemplateResponse> templates = List.of(
        new TemplateResponse(1L, "Template 1", 1L, List.of()),
        new TemplateResponse(2L, "Template 2", 1L, List.of())
    );

    when(templateService.getTemplatesByUserId(any())).thenReturn(templates);

    mockMvc.perform(get("/api/v1/templates"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Template 1"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].name").value("Template 2"));

    verify(templateService, times(1)).getTemplatesByUserId(any());
  }

  // --------------- GET TEMPLATE BY ID ----------------------

  @Test
  @DisplayName("should return template by ID when it exists and user has access")
  void shouldReturnTemplateByIdWhenExistsAndUserHasAccess() throws Exception {
    TemplateResponse template = new TemplateResponse(5L, "Sample Template", 1L, List.of());

    when(templateService.getTemplateById(any(), any())).thenReturn(template);

    mockMvc.perform(get("/api/v1/templates/5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(5))
        .andExpect(jsonPath("$.name").value("Sample Template"));

    verify(templateService, times(1)).getTemplateById(any(), any());
  }

  @Test
  @DisplayName("should return 404 when template not found")
  void shouldReturn404WhenTemplateNotFound() throws Exception {
    when(templateService.getTemplateById(any(), any()))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));

    mockMvc.perform(get("/api/v1/templates/100"))
        .andExpect(status().isNotFound());

    verify(templateService, times(1)).getTemplateById(any(), any());
  }

  @Test
  @DisplayName("should return 403 when user tries to access another user's template")
  void shouldReturn403WhenAccessingAnotherUsersTemplate() throws Exception {
    when(templateService.getTemplateById(any(), any()))
        .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

    mockMvc.perform(get("/api/v1/templates/10"))
        .andExpect(status().isForbidden());

    verify(templateService, times(1)).getTemplateById(any(), any());
  }

  @Test
  @DisplayName("should allow admin to get any template")
  void shouldAllowAdminToGetAnyTemplate() throws Exception {
    CustomUserDetails adminUser = mock(CustomUserDetails.class);
    when(adminUser.getId()).thenReturn(999L);
    when(adminUser.getRole()).thenReturn("ADMIN");

    SecurityContextHolder.getContext().setAuthentication(
        new TestingAuthenticationToken(adminUser, null, "ROLE_ADMIN")
    );

    TemplateResponse template = new TemplateResponse(7L, "Admin Template", 1L, List.of());
    when(templateService.getTemplateById(any(), any())).thenReturn(template);

    mockMvc.perform(get("/api/v1/templates/7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(7));
  }
  // --------------- DELETE TEMPLATE ----------------------

  @Test
  @DisplayName("should delete template successfully when user is owner")
  void shouldDeleteTemplateSuccessfully() throws Exception {
    doNothing().when(templateService).deleteTemplate(any(), any());

    mockMvc.perform(delete("/api/v1/templates/7").with(csrf()))
        .andExpect(status().isNoContent());

    verify(templateService, times(1)).deleteTemplate(any(), any());
  }

  @Test
  @DisplayName("should return 404 when trying to delete non-existing template")
  void shouldReturn404WhenDeletingNonExistingTemplate() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"))
        .when(templateService).deleteTemplate(any(), any());

    mockMvc.perform(delete("/api/v1/templates/99").with(csrf()))
        .andExpect(status().isNotFound());

    verify(templateService, times(1)).deleteTemplate(any(), any());
  }

  @Test
  @DisplayName("should return 403 when trying to delete template of another user")
  void shouldReturn403WhenDeletingTemplateOfAnotherUser() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"))
        .when(templateService).deleteTemplate(any(), any());

    mockMvc.perform(delete("/api/v1/templates/10").with(csrf()))
        .andExpect(status().isForbidden());

    verify(templateService, times(1)).deleteTemplate(any(), any());
  }


}