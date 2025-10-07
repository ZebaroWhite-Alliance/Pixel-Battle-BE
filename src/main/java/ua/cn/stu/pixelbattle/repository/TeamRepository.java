package ua.cn.stu.pixelbattle.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.cn.stu.pixelbattle.model.Team;

/**
 * Repository for accessing Team entities.
 * Provides basic CRUD operations and a method to find a team by its name.
 */
public interface TeamRepository extends JpaRepository<Team, Long> {

  /**
   * Finds a team by its unique name.
   *
   * @param name the name of the team
   * @return an Optional containing the team if found, or empty if not
   */
  Optional<Team> findByName(String name);
}
