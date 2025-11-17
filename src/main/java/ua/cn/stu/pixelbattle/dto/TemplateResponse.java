package ua.cn.stu.pixelbattle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for template response data.
 * Contains template information including ID, name, owner ID, and pixel data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateResponse {

  private Long id;
  private String name;

  @JsonProperty("user_id")
  private Long userId;

  private List<PixelResponse> pixels;
}
