package ua.cn.stu.pixelbattle.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO containing basic game info: field size and cooldown.
 */
@Data
@AllArgsConstructor
public class GameInfoResponse {
  private int width;
  private int height;
  private int cooldown; // in sec
}
