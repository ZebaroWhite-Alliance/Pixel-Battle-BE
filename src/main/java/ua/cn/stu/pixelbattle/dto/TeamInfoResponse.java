package ua.cn.stu.pixelbattle.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing information about a team.
 * Includes the team name, creation date, and list of member usernames.
 */
@Data
@AllArgsConstructor
public class TeamInfoResponse {
  private String name;
  private LocalDateTime createdAt;
  private List<String> members;
}
