package ua.cn.stu.pixelbattle.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
