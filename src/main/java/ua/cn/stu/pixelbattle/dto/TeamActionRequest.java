package ua.cn.stu.pixelbattle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for actions on a team (join or leave).
 * Contains the name of the target team.
 */
@Data
public class TeamActionRequest {
  @NotBlank(message = "Team name is required")
  private String name;
}
