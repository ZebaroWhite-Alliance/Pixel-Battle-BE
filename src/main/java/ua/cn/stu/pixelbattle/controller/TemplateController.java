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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
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
   * <p>The template data is provided in the request body, and the user ID is
   * extracted from the authenticated principal.</p>
   *
   * @param userDetails the authenticated user details
   * @param template the {@link Template} data to create
   * @return the created {@link Template} instance
   */
  @PostMapping
  public Template createTemplate(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody Template template
  ) {
    return templateService.createTemplate(template, userDetails.getId());
  }

  /**
   * Retrieves all templates belonging to the authenticated user.
   *
   * <p>Each template contains its metadata and pixel data as stored in the database.</p>
   *
   * @param userDetails the authenticated user details
   * @return a list of {@link Template} objects owned by the user
   */
  @GetMapping
  public List<Template> getMyTemplates(@AuthenticationPrincipal CustomUserDetails userDetails) {
    return templateService.getTemplatesByUserId(userDetails.getId());
  }

  /**
   * Retrieves a template by its unique identifier.
   *
   * <p>This endpoint does not require authentication and allows any user
   * to view a specific template by ID.</p>
   *
   * @param id the unique identifier of the template
   * @return the corresponding {@link Template} instance
   * @throws ResponseStatusException if the template is not found
   */
  @GetMapping("/{id}")
  public Template getTemplateById(@PathVariable Long id) {
    return templateService.getTemplateById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));
  }

  /**
   * Deletes a template by its unique identifier.
   *
   * <p>Only the owner of the template can delete it.</p>
   *
   * @param id the unique identifier of the template to delete
   */
  @DeleteMapping("/{id}")
  public void deleteTemplate(@PathVariable Long id) {
    templateService.deleteTemplate(id);
  }
}
