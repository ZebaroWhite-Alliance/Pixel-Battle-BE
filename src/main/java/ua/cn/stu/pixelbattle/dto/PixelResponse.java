package ua.cn.stu.pixelbattle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a pixel on the board along with the user who changed it.
 *
 * <p>Contains coordinates, color, and username of the user who changed the pixel.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PixelResponse {

  @JsonProperty("x")
  private int coordinateX;

  @JsonProperty("y")
  private int coordinateY;

  private String color;
}
