package ua.cn.stu.pixelbattle.service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.UserRepository;

/**
 * Service for managing {@link User} entities.
 *
 * <p>Provides methods to retrieve users by their unique identifier or username.
 * All methods return {@link Optional} to safely handle the case when a user
 * does not exist in the database.
 */
@Service
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  /**
   * Retrieves a user by their unique ID.
   *
   * @param id the unique identifier of the user
   * @return an {@link Optional} containing the {@link User} if found, or empty if not
   */
  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  /**
   * Retrieves a user by their username.
   *
   * @param username the username of the user
   * @return an {@link Optional} containing the {@link User} if found, or empty if not
   */
  public Optional<User> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

}