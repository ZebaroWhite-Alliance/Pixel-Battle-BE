package ua.cn.stu.pixelbattle.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ua.cn.stu.pixelbattle.dto.CreateTemplateRequest;
import ua.cn.stu.pixelbattle.dto.TemplateResponse;
import ua.cn.stu.pixelbattle.model.Template;
import ua.cn.stu.pixelbattle.repository.TemplateRepository;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;


/**
 * Unit tests for {@link TemplateService} covering template management business logic.
 *
 * <p>Tests validate template creation, retrieval, and deletion operations with proper
 * error handling and access control. Uses Mockito for isolated service testing without
 * database dependencies.</p>
 */
@ExtendWith(MockitoExtension.class)
public class TemplateServiceTest {

  @Mock
  private TemplateRepository templateRepository;

  @InjectMocks
  private TemplateService templateService;

  // --------------- CREATE TEMPLATE ---------------

  @Test
  @DisplayName("Should create template with given name and userId")
  void shouldCreateTemplateWithGivenName() {
    Long userId = 1L;

    Template saved = new Template();
    saved.setId(10L);
    saved.setName("My Template");
    saved.setUserId(userId);
    saved.setPixels(List.of());

    when(templateRepository.save(any(Template.class))).thenReturn(saved);

    CreateTemplateRequest input = new CreateTemplateRequest("My Template", List.of());
    TemplateResponse result = templateService.createTemplate(input, userId);

    assertNotNull(result);
    assertEquals("My Template", result.getName());
    assertEquals(userId, result.getUserId());
    assertEquals(10L, result.getId());

    ArgumentCaptor<Template> templateCaptor = ArgumentCaptor.forClass(Template.class);
    verify(templateRepository).save(templateCaptor.capture());
    Template captured = templateCaptor.getValue();
    assertEquals("My Template", captured.getName());
    assertEquals(userId, captured.getUserId());
  }

  @Test
  @DisplayName("Should generate name when name is null")
  void shouldGenerateNameWhenNameIsNull() {
    CreateTemplateRequest input = new CreateTemplateRequest(null, List.of());
    Long userId = 1L;

    when(templateRepository.save(any(Template.class))).thenAnswer(invocation -> {
      Template template = invocation.getArgument(0);
      template.setId(10L);
      return template;
    });

    TemplateResponse result = templateService.createTemplate(input, userId);

    assertNotNull(result);
    assertTrue(result.getName().startsWith("template-"));
    assertFalse(result.getName().isEmpty());
  }

  @Test
  @DisplayName("Should generate name when name is blank")
  void shouldGenerateNameWhenNameIsBlank() {
    CreateTemplateRequest input = new CreateTemplateRequest("   ", List.of());
    Long userId = 1L;

    when(templateRepository.save(any(Template.class))).thenAnswer(invocation -> {
      Template template = invocation.getArgument(0);
      template.setId(10L);
      return template;
    });

    TemplateResponse result = templateService.createTemplate(input, userId);

    assertNotNull(result);
    assertTrue(result.getName().startsWith("template-"));
  }

  // --------------- GET TEMPLATES BY USER ID ---------------

  @Test
  @DisplayName("Should return templates by user id")
  void shouldReturnTemplatesByUserId() {
    Long userId = 1L;

    Template template1 = new Template();
    template1.setId(1L);
    template1.setName("Template 1");
    template1.setUserId(userId);

    Template template2 = new Template();
    template2.setId(2L);
    template2.setName("Template 2");
    template2.setUserId(userId);

    when(templateRepository.findByUserId(userId)).thenReturn(List.of(template1, template2));

    List<TemplateResponse> result = templateService.getTemplatesByUserId(userId);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Template 1", result.get(0).getName());
    assertEquals("Template 2", result.get(1).getName());
  }

  @Test
  @DisplayName("Should return empty list when user has no templates")
  void shouldReturnEmptyListWhenNoTemplates() {
    Long userId = 1L;
    when(templateRepository.findByUserId(userId)).thenReturn(List.of());

    List<TemplateResponse> result = templateService.getTemplatesByUserId(userId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  // --------------- GET TEMPLATE BY ID ---------------

  @Test
  @DisplayName("Should return template when user is owner")
  void shouldReturnTemplateWhenUserIsOwner() {
    Long templateId = 1L;
    Long userId = 1L;

    CustomUserDetails userDetails = mock(CustomUserDetails.class);
    when(userDetails.getId()).thenReturn(userId);
    when(userDetails.getRole()).thenReturn("USER");

    Template template = new Template();
    template.setId(templateId);
    template.setName("My Template");
    template.setUserId(userId);

    when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));

    TemplateResponse result = templateService.getTemplateById(templateId, userDetails);

    assertNotNull(result);
    assertEquals(templateId, result.getId());
    assertEquals("My Template", result.getName());
  }

  @Test
  @DisplayName("Should return template when user is admin")
  void shouldReturnTemplateWhenUserIsAdmin() {
    Long templateId = 1L;
    CustomUserDetails adminDetails = mock(CustomUserDetails.class);
    when(adminDetails.getRole()).thenReturn("ADMIN");

    Template template = new Template();
    template.setId(templateId);
    template.setName("Admin Template");

    template.setUserId(1L);

    when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));

    TemplateResponse result = templateService.getTemplateById(templateId, adminDetails);

    assertNotNull(result);
    assertEquals(templateId, result.getId());
  }

  @Test
  @DisplayName("Should throw 404 when template not found")
  void shouldThrowNotFoundWhenTemplateNotFound() {
    Long templateId = 999L;
    CustomUserDetails userDetails = mock(CustomUserDetails.class);

    when(templateRepository.findById(templateId)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> templateService.getTemplateById(templateId, userDetails));

    assertEquals(404, exception.getStatusCode().value());
  }

  @Test
  @DisplayName("Should throw 403 when user accesses another user's template")
  void shouldThrowForbiddenWhenAccessingAnotherUserTemplate() {
    Long templateId = 1L;
    Long otherUserId = 2L;

    CustomUserDetails userDetails = mock(CustomUserDetails.class);
    when(userDetails.getId()).thenReturn(otherUserId);
    when(userDetails.getRole()).thenReturn("USER");

    Long ownerId = 1L;

    Template template = new Template();
    template.setId(templateId);
    template.setName("Other User Template");
    template.setUserId(ownerId);

    when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> templateService.getTemplateById(templateId, userDetails));

    assertEquals(403, exception.getStatusCode().value());
  }

  // --------------- DELETE TEMPLAET ---------------

  @Test
  @DisplayName("Should delete template when user is owner")
  void shouldDeleteTemplateWhenUserIsOwner() {
    Long templateId = 1L;
    Long userId = 1L;

    Template template = new Template();
    template.setId(templateId);
    template.setName("My Template");
    template.setUserId(userId);

    when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
    doNothing().when(templateRepository).delete(template);

    templateService.deleteTemplate(templateId, userId);

    verify(templateRepository).delete(template);
  }

  @Test
  @DisplayName("Should throw 404 when deleting non-existent template")
  void shouldThrowNotFoundWhenDeletingNonExistentTemplate() {
    Long templateId = 999L;
    Long userId = 1L;

    when(templateRepository.findById(templateId)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> templateService.deleteTemplate(templateId, userId));

    assertEquals(404, exception.getStatusCode().value());
    verify(templateRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Should throw 403 when deleting another user's template")
  void shouldThrowForbiddenWhenDeletingAnotherUserTemplate() {
    Long templateId = 1L;
    Long ownerId = 1L;

    Template template = new Template();
    template.setId(templateId);
    template.setName("Other User Template");
    template.setUserId(ownerId);

    when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));

    Long otherUserId = 2L;
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> templateService.deleteTemplate(templateId, otherUserId));

    assertEquals(403, exception.getStatusCode().value());
    verify(templateRepository, never()).delete(any());
  }
}