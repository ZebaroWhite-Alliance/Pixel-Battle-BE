package ua.cn.stu.pixelbattle.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating new template requests.
 * Contains template name and pixel data for template creation.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTemplateRequest {

  private String name;
  private List<PixelResponse> pixels;

}
