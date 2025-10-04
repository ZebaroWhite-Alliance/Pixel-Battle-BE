package ua.cn.stu.pixelbattle.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing a single pixel change in history.
 *
 * <p>Contains the ID of the change, pixel coordinates, and the new color.</p>
 */
@Data
@AllArgsConstructor
public class PixelHistoryDto {
  private Long id;
  private int x;
  private int y;
  private String newColor;
}
