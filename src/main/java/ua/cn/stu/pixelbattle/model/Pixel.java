package ua.cn.stu.pixelbattle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single pixel on the board.
 *
 * <p>Contains the pixel's coordinates, its current color, and the username
 * of the user who last modified it.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pixel {
  private int x;
  private int y;
  private String color;
  private String username;
}