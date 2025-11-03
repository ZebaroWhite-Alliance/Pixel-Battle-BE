package ua.cn.stu.pixelbattle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.UserRepository;

/**
 * Unit tests for {@link UserService}.
 *
 * <p>Verifies main service scenarios:
 * <ul>
 *   <li>Retrieving a user by ID when user exists or does not exist</li>
 *   <li>Retrieving a user by username when user exists or does not exist</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  UserService userService;

  @Test
  @DisplayName("should return user when user exists by ID")
  void shouldReturnUserWhenExistsById() {
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    Optional<User> result = userService.getUserById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    assertEquals("testUser", result.get().getUsername());
  }

  @Test
  @DisplayName("should return empty when user does not exist by ID")
  void shouldReturnEmptyWhenNotExistsById() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<User> result = userService.getUserById(1L);

    assertTrue(result.isEmpty());
  }


  @Test
  @DisplayName("should return user when user exists by username")
  void shouldReturnUserWhenExistsByUsername() {
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");

    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

    Optional<User> result = userService.getUserByUsername("testUser");

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    assertEquals("testUser", result.get().getUsername());
  }

  @Test
  @DisplayName("should return empty when user does not exist by username")
  void shouldReturnEmptyWhenNotExistsByUsername() {
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

    Optional<User> result = userService.getUserByUsername("testUser");

    assertTrue(result.isEmpty());
  }
}
