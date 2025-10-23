package ua.cn.stu.pixelbattle.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO representing a pixel change request.
 *
 * <p>Contains only the coordinates and the new color.
 * User info is inferred from the JWT token on the server side.</p>
 */
@Data
public class PixelChangeRequest {
  @Min(0)
  private int coordinateX;
  @Min(0)
  private int coordinateY;
  @NotBlank
  @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be in hex format like #FFFFFF")
  private String color;
}
