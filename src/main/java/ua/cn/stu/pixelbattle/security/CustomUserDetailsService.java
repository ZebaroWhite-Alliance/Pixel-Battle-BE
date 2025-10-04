package ua.cn.stu.pixelbattle.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.UserRepository;

/**
 * Service implementation of {@link UserDetailsService} used by Spring Security
 * to retrieve user authentication and authorization information.
 *
 * <p>This class loads a {@link User} entity from the database based on the given
 * username and wraps it into a {@link CustomUserDetails} object, which is then
 * used by Spring Security during the authentication process.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Loads user-specific data by username.
   *
   * @param username the username identifying the user whose data is required
   * @return a fully populated {@link UserDetails} object with authentication
   *     and authorization information
   * @throws UsernameNotFoundException if no user with the given username exists
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return new CustomUserDetails(user);
  }
}