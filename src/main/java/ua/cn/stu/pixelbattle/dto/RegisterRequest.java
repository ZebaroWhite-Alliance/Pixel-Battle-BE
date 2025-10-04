package ua.cn.stu.pixelbattle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request.
 *
 * <p>Contains the username and password provided by the client.
 *Password must be at least 8 characters long and include uppercase,
 * lowercase letters, and digits.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
  private String username;

  @NotBlank(message = "Password is required")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
      message = "Password must be at least 8 characters"
          + " long and include uppercase, lowercase letters, and digits"
  )
  private String password;



}
