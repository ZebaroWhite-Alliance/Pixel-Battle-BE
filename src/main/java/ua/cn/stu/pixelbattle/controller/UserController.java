package ua.cn.stu.pixelbattle.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixelbattle.dto.PixelHistoryDto;
import ua.cn.stu.pixelbattle.dto.UserResponse;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;
import ua.cn.stu.pixelbattle.service.PixelHistoryService;


/**
 * REST controller for user-related operations.
 *
 * <p>Provides endpoints to retrieve information about the currently
 * authenticated user and their pixel change history.</p>
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
  private final PixelHistoryService pixelHistoryService;

  /**
   * Retrieves information about the currently authenticated user.
   *
   * @param userDetails details of the authenticated user, injected by Spring Security
   * @return {@link ResponseEntity} containing:
   *     <ul>
   *     <li>{@link UserResponse} with user details if authenticated</li>
   *     <li>HTTP 401 Unauthorized if no user is authenticated</li>
   *     </ul>
   */
  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails != null) {
      UserResponse response = new UserResponse(
          userDetails.getId(),
          userDetails.getUsername(),
          userDetails.getPixelChangesCount()
      );
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user authenticated");
  }

  /**
   * Retrieves the pixel change history of the currently authenticated user.
   *
   * @param userDetails details of the authenticated user, injected by Spring Security
   * @return {@link ResponseEntity} containing:
   *     <ul>
   *     <li>List of {@link PixelHistoryDto} if history exists</li>
   *     <li>HTTP 404 Not Found if no history exists</li>
   *     <li>HTTP 401 Unauthorized if no user is authenticated</li>
   *     </ul>
   */
  @GetMapping("/history")
  public ResponseEntity<?> getPixelHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails != null) {
      List<PixelHistoryDto> history = pixelHistoryService.getHistoryByUserId(userDetails.getId());
      if (history.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No pixel history found for this user.");
      }
      return ResponseEntity.ok(history);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user authenticated");
  }
}
