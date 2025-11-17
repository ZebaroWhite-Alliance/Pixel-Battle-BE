package ua.cn.stu.pixelbattle.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTemplateRequest {

  private String name;
  private List<PixelResponse> pixels;

}
