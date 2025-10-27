package ua.cn.stu.pixelbattle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for authentication requests.
 *
 * <p>Contains the credentials required for user login:
 * username and password. Used in the authentication process
 * to validate user identity.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
  private String username;
  private String password;
}
