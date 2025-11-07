package ua.cn.stu.pixelbattle.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.cn.stu.pixelbattle.model.Template;

/**
 * Repository interface for accessing {@link Template} entities.
 *
 * <p>Provides CRUD operations and custom queries for managing user templates.
 * Extends {@link JpaRepository} to leverage standard JPA functionality.</p>
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

  /**
   * Retrieves all templates belonging to a specific user.
   *
   * @param userId the ID of the user
   * @return a list of {@link Template} objects owned by the given user
   */
  List<Template> findByUserId(Long userId);
}