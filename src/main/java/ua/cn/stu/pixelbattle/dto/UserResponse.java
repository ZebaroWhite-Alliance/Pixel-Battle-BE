package ua.cn.stu.pixelbattle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing user information.
 *
 * <p>Contains user ID, username, and the count of pixel changes made by the user.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private Long userId;
  private String username;
  private int pixelChangesCount;

}
