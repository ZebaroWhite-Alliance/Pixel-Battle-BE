package ua.cn.stu.pixelbattle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating a new team.
 * Contains the name of the team.
 */
@Data
public class CreateTeamRequest {
  @NotBlank(message = "Team name is required")
  private String name;
}
