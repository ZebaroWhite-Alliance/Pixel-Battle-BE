package ua.cn.stu.pixelbattle.security;

import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.cn.stu.pixelbattle.model.User;


/**
 * Custom implementation of {@link UserDetails} that wraps the {@link User} entity.
 *
 * <p>This class provides user authentication information for Spring Security.
 * It stores the user's ID, username, password hash, role, and pixel changes count.
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final Long id;
  private final String username;
  private final String password;
  private final String role;
  private final int pixelChangesCount;

  /**
   * Constructs a {@code CustomUserDetails} from a {@link User} entity.
   *
   * @param user the {@link User} entity to wrap
   */
  public CustomUserDetails(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.password = user.getPasswordHash();
    this.role = user.getRole();
    this.pixelChangesCount = user.getPixelChangesCount();
  }


  /**
   * Returns the authorities granted to the user.
   *
   * <p>Currently returns an empty list.</p>
   *
   * @return a collection of granted authorities
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  /**
   * Indicates whether the user's account has expired.
   *
   * @return {@code true} if account is non-expired
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is locked or unlocked.
   *
   * @return {@code true} if account is not locked
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indicates whether the user's credentials (password) has expired.
   *
   * @return {@code true} if credentials are non-expired
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is enabled or disabled.
   *
   * @return {@code true} if the user is enabled
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}