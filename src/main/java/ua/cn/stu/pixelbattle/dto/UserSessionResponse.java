package ua.cn.stu.pixelbattle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for returning basic user session information.
 *
 * <p>This DTO is used by the {@code /session} endpoint to provide minimal
 * details about the currently logged-in user.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionResponse {
  private Long id;
  private String username;
}
