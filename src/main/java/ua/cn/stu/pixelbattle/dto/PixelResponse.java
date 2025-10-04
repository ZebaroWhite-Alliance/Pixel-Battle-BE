package ua.cn.stu.pixelbattle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing a pixel on the board along with the user who changed it.
 *
 * <p>Contains coordinates, color, and username of the user who changed the pixel.</p>
 */
@Data
@AllArgsConstructor
public class PixelResponse {
  private int x;
  private int y;
  private String color;
  private String username;
}
