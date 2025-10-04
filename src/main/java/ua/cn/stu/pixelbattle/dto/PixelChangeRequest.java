package ua.cn.stu.pixelbattle.dto;

import lombok.Data;

/**
 * DTO representing a pixel change request.
 *
 * <p>Contains only the coordinates and the new color.
 * User info is inferred from the JWT token on the server side.</p>
 */
@Data
public class PixelChangeRequest {
  private int x;
  private int y;
  private String color;
}
