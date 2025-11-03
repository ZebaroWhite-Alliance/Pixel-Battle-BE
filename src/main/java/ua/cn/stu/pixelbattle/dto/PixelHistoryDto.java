package ua.cn.stu.pixelbattle.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a single pixel change in history.
 *
 * <p>Contains the ID of the change, pixel coordinates, and the new color.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PixelHistoryDto {
  private Long id;

  @JsonProperty("x")
  private int coordinateX;

  @JsonProperty("y")
  private int coordinateY;

  @JsonProperty("new_color")
  private String newColor;
}
