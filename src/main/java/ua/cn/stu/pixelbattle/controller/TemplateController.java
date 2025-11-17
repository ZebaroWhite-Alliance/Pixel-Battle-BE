package ua.cn.stu.pixelbattle.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ua.cn.stu.pixelbattle.dto.CreateTemplateRequest;
import ua.cn.stu.pixelbattle.dto.TemplateResponse;
import ua.cn.stu.pixelbattle.model.Template;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;
import ua.cn.stu.pixelbattle.service.TemplateService;

/**
 * Controller for managing user templates.
 *
 * <p>Provides APIs for creating, retrieving, and deleting pixel art templates.
 * All endpoints (except fetching a template by ID) require authentication.</p>
 */
@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

  private final TemplateService templateService;

  /**
   * Creates a new template for the authenticated user.
   *
   * @param userDetails the authenticated user details
   * @param request the template creation request
   * @return the created template response
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TemplateResponse createTemplate(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody CreateTemplateRequest request
  ) {
    return templateService.createTemplate(request, userDetails.getId());
  }

  /**
   * Retrieves all templates belonging to the authenticated user.
   *
   * @param userDetails the authenticated user details
   * @return list of user's templates
   */
  @GetMapping
  public List<TemplateResponse> getMyTemplates(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    return templateService.getTemplatesByUserId(userDetails.getId());
  }

  /**
   * Retrieves a specific template by ID with access control.
   * Users can access their own templates, admins can access any.
   *
   * @param userDetails the authenticated user details
   * @param id the template ID
   * @return the template response
   * @throws ResponseStatusException if template not found or access denied
   */
  @GetMapping("/{id}")
  public TemplateResponse getTemplateById(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long id
  ) {
    return templateService.getTemplateById(id, userDetails);
  }


  /**
   * Deletes a template by ID. Users can only delete their own templates.
   *
   * @param userDetails the authenticated user details
   * @param id the template ID to delete
   * @throws ResponseStatusException if template not found or access denied
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTemplate(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long id
  ) {
    templateService.deleteTemplate(id, userDetails.getId());
  }
}
