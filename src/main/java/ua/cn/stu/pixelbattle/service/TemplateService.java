package ua.cn.stu.pixelbattle.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.cn.stu.pixelbattle.dto.CreateTemplateRequest;
import ua.cn.stu.pixelbattle.dto.PixelResponse;
import ua.cn.stu.pixelbattle.dto.TemplateResponse;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.model.Pixel;
import ua.cn.stu.pixelbattle.model.Template;
import ua.cn.stu.pixelbattle.repository.TemplateRepository;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;

/**
 * Service layer for managing {@link Template} entities.
 *
 * <p>Provides business logic for creating, retrieving, and deleting templates.
 * Handles template ownership and assigns default names when none are provided.</p>
 */
@Service
@RequiredArgsConstructor
public class TemplateService {

  private final TemplateRepository templateRepository;

  /**
   * Creates a new template for the specified user.
   * Generates a default name if none provided.
   *
   * @param request the template creation data
   * @param userId the ID of the user creating the template
   * @return the created template response
   */
  public TemplateResponse createTemplate(CreateTemplateRequest request, Long userId) {
    String name = (request.getName() == null || request.getName().isBlank())
        ? "template-" + System.currentTimeMillis()
        : request.getName();

    Template template = new Template();
    template.setName(name);
    template.setUserId(userId);
    template.setPixels(request.getPixels());

    Template saved = templateRepository.save(template);
    return toTemplateResponse(saved);
  }

  /**
   * Retrieves all templates belonging to a specific user.
   *
   * @param userId the ID of the user
   * @return list of user's templates
   */
  public List<TemplateResponse> getTemplatesByUserId(Long userId) {
    return templateRepository.findByUserId(userId)
        .stream()
        .map(this::toTemplateResponse)
        .toList();
  }

  /**
   * Retrieves a template by ID with access control.
   * Users can access their own templates, admins can access any.
   *
   * @param id the template ID
   * @param userDetails the authenticated user details
   * @return the template response
   * @throws ResponseStatusException if template not found or access denied
   */
  public TemplateResponse getTemplateById(Long id, CustomUserDetails userDetails) {
    Template template = templateRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));

    if (!userDetails.getRole().equals("ADMIN")
        && !template.getUserId().equals(userDetails.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    return toTemplateResponse(template);
  }

  /**
   * Deletes a template by ID. Users can only delete their own templates.
   *
   * @param templateId the template ID to delete
   * @param userId the ID of the user attempting deletion
   * @throws ResponseStatusException if template not found or access denied
   */
  public void deleteTemplate(Long templateId, Long userId) {
    Template template = templateRepository.findById(templateId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));

    if (!template.getUserId().equals(userId)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You can only delete your own templates");
    }

    templateRepository.delete(template);
  }

  private TemplateResponse toTemplateResponse(Template template) {
    TemplateResponse response = new TemplateResponse();
    response.setId(template.getId());
    response.setName(template.getName());
    response.setUserId(template.getUserId());
    response.setPixels(template.getPixels());
    return response;
  }
}
