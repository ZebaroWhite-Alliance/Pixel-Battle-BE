package ua.cn.stu.pixelbattle.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.model.Template;
import ua.cn.stu.pixelbattle.repository.TemplateRepository;

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
   * Creates and saves a new template for the specified user.
   *
   * <p>If no name is provided, a default name is generated based on the current timestamp.</p>
   *
   * @param template the {@link Template} object to create
   * @param userId the ID of the user who owns the template
   * @return the saved {@link Template} instance
   */
  public Template createTemplate(Template template, Long userId) {
    template.setUserId(userId);
    if (template.getName() == null || template.getName().isBlank()) {
      template.setName("template-" + System.currentTimeMillis());
    }
    return templateRepository.save(template);
  }

  /**
   * Retrieves all templates created by a specific user.
   *
   * @param userId the ID of the user
   * @return a list of {@link Template} objects owned by the user
   */
  public List<Template> getTemplatesByUserId(Long userId) {
    return templateRepository.findByUserId(userId);
  }

  /**
   * Retrieves a template by its unique identifier.
   *
   * @param id the ID of the template
   * @return an {@link Optional} containing the template if found, or empty otherwise
   */
  public Optional<Template> getTemplateById(Long id) {
    return templateRepository.findById(id);
  }

  /**
   * Deletes a template by its unique identifier.
   *
   * @param id the ID of the template to delete
   */
  public void deleteTemplate(Long id) {
    templateRepository.deleteById(id);
  }
}
