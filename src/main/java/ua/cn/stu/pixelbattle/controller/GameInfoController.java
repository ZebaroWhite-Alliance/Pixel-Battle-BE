package ua.cn.stu.pixelbattle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixelbattle.dto.GameInfoResponse;
import ua.cn.stu.pixelbattle.service.PixelService;

/**
 * REST controller for retrieving general information about the game field and settings.
 *
 * <p>Provides an endpoint to fetch game configuration
 *    such as field size, cooldown, and other parameters
 * needed by the frontend to initialize the game board.</p>
 *
 * <p>Endpoint:</p>
 * <ul>
 *   <li>GET /api/v1/info – returns {@link GameInfoResponse} with the current game settings</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GameInfoController {
  private final PixelService pixelService;

  /**
   * Returns general information about the game field and configuration.
   *
   * <p>This includes field width, height, cooldown in seconds, and any other settings
   * needed by the frontend.</p>
   *
   * @return a {@link GameInfoResponse} object containing current game settings
   */
  @GetMapping("/info")
  public GameInfoResponse getInfo() {
    return pixelService.getGameInfo();
  }

}
