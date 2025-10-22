package ua.cn.stu.pixelbattle.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.cn.stu.pixelbattle.model.User;


/**
 * Repository interface for {@link User} entities.
 *
 * <p>Provides CRUD operations and custom queries to find users by username.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a user by their username.
   *
   * @param username the username to search for
   * @return an {@link Optional} containing the user if found, otherwise empty
   */
  Optional<User> findByUsername(String username);

  /**
   * Checks if a user with the given username exists.
   *
   * @param username the username to check
   * @return true if a user with the username exists, false otherwise
   */
  boolean existsByUsername(String username);
}
