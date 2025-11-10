package ua.cn.stu.pixelbattle.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixelbattle.dto.PixelHistoryDto;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;
import ua.cn.stu.pixelbattle.service.PixelHistoryService;


/**
 * REST controller for retrieving pixel history records.
 *
 * <p>Provides endpoints for fetching global pixel history and individual user's pixel history.
 * Access to other users' history is restricted to admin users.</p>
 */
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
@Validated
public class PixelHistoryController {

  private final PixelHistoryService pixelHistoryService;

  /**
   * Retrieves a list of all pixel history records after a given ID.
   *
   * @param fromId the starting pixel history ID (exclusive)
   * @param limit  the maximum number of records to fetch, defaults to 10,000
   * @return a {@link ResponseEntity} containing the list of {@link PixelHistoryDto} objects
   */
  @GetMapping
  public ResponseEntity<List<PixelHistoryDto>> getAllAfter(
      @RequestParam long fromId,
      @RequestParam(defaultValue = "10000") int limit
  ) {
    List<PixelHistoryDto> history = pixelHistoryService.getAllAfter(fromId, limit);
    return ResponseEntity.ok(history);
  }

  /**
   * Retrieves pixel history records for a specific user.
   *
   * <p>If {@code userId} is {@code null}, retrieves the history for the current user.
   * Non-admin users cannot access history of other users.</p>
   *
   * @param currentUser the currently authenticated user
   * @param userId      the ID of the target user, or {@code null} to fetch current user's history
   * @param fromId      the starting pixel history ID (exclusive)
   * @param limit       the maximum number of records to fetch, defaults to 1,000
   * @return a {@link ResponseEntity} containing the list of {@link PixelHistoryDto} objects,
   *     or a 401/403 status if access is denied
   */
  @GetMapping("/user")
  public ResponseEntity<List<PixelHistoryDto>> getUserHistory(
      @AuthenticationPrincipal CustomUserDetails currentUser,
      @RequestParam(required = false) Long userId,
      @RequestParam long fromId,
      @RequestParam(defaultValue = "1000") int limit
  ) {
    List<PixelHistoryDto> history =
        pixelHistoryService.getUserHistory(currentUser, userId, fromId, limit);
    return ResponseEntity.ok(history);
  }
}
