package ua.cn.stu.pixelbattle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ua.cn.stu.pixelbattle.dto.PixelHistoryDto;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.model.PixelHistory;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.PixelHistoryRepository;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;

/**
 * Unit tests for {@link PixelHistoryService}.
 *
 * <p>Verifies main service scenarios:
 * <ul>
 *   <li>Fetching global pixel history</li>
 *   <li>Fetching user-specific history</li>
 *   <li>Access control for admin and non-admin users</li>
 *   <li>Handling invalid parameters and exceptions</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class PixelHistoryServiceTest {

  @Mock
  private PixelHistoryRepository pixelHistoryRepository;

  @InjectMocks
  private PixelHistoryService pixelHistoryService;

  // ---------- GET PUBLIC HISTORY ----------

  @Test
  @DisplayName("should return pixel history DTOs when valid fromId and limit provided")
  void shouldReturnPixelHistoryDtosWhenValidParams() {
    PixelHistory p1 = new PixelHistory();
    p1.setId(1L);
    p1.setCoordinateX(5);
    p1.setCoordinateY(10);
    p1.setNewColor("#ABCDEF");

    when(pixelHistoryRepository.findAllAfter(10L, 1000))
        .thenReturn(List.of(p1));

    List<PixelHistoryDto> result = pixelHistoryService.getAllAfter(10L, 1000);

    assertEquals(1, result.size());
    assertEquals(5, result.get(0).getCoordinateX());
    assertEquals("#ABCDEF", result.get(0).getNewColor());
  }

  @Test
  @DisplayName("should apply max limit when provided limit exceeds MAX_LIMIT")
  void shouldApplyMaxLimitWhenTooHigh() {
    PixelHistory p = new PixelHistory();
    p.setId(1L);
    p.setCoordinateX(1);
    p.setCoordinateY(2);
    p.setNewColor("#FFF000");

    when(pixelHistoryRepository.findAllAfter(0L, 10000)).thenReturn(List.of(p));

    List<PixelHistoryDto> result = pixelHistoryService.getAllAfter(0L, 999_999);

    assertEquals(1, result.size());
    assertEquals("#FFF000", result.get(0).getNewColor());
  }

  @Test
  @DisplayName("should return empty list when no pixels found after fromId")
  void shouldReturnEmptyListWhenNoPixelsFound() {
    when(pixelHistoryRepository.findAllAfter(10L, 1000)).thenReturn(List.of());

    List<PixelHistoryDto> result = pixelHistoryService.getAllAfter(10L, 1000);

    assertTrue(result.isEmpty());
  }

  // ---------- GET USER HISTORY ----------

  @Test
  @DisplayName("should return current user's history when userId is null")
  void shouldReturnCurrentUserHistoryWhenUserIdIsNull() {
    PixelHistory ph = new PixelHistory();
    ph.setId(10L);
    ph.setCoordinateX(3);
    ph.setCoordinateY(4);
    ph.setNewColor("#123456");

    when(pixelHistoryRepository.findAllByUserAfter(1L, 0L, 1000))
        .thenReturn(List.of(ph));

    CustomUserDetails user = new CustomUserDetails(1L, "user", "pass", "USER", 5);
    List<PixelHistoryDto> result =
        pixelHistoryService.getUserHistory(user, null, 0L, 1000);

    assertEquals(1, result.size());
    assertEquals("#123456", result.get(0).getNewColor());
    assertEquals(3, result.get(0).getCoordinateX());
  }

  @Test
  @DisplayName("should return empty list when user has no pixel history")
  void shouldReturnEmptyListWhenUserHasNoHistory() {
    when(pixelHistoryRepository.findAllByUserAfter(1L, 0L, 1000))
        .thenReturn(List.of());

    CustomUserDetails user = new CustomUserDetails(1L, "user", "pass", "USER", 5);
    List<PixelHistoryDto> result =
        pixelHistoryService.getUserHistory(user, null, 0L, 1000);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should use MAX_LIMIT when limit is greater than allowed")
  void shouldUseMaxLimitWhenLimitTooBig() {
    PixelHistory ph = new PixelHistory();
    ph.setCoordinateX(10);
    ph.setCoordinateY(20);
    ph.setNewColor("#FFFF00");

    when(pixelHistoryRepository.findAllByUserAfter(1L, 0L, 10000))
        .thenReturn(List.of(ph));


    CustomUserDetails user = new CustomUserDetails(1L, "user", "pass", "USER", 5);
    List<PixelHistoryDto> result =
        pixelHistoryService.getUserHistory(user, null, 0L, 999_999);

    assertEquals(1, result.size());
    assertEquals("#FFFF00", result.get(0).getNewColor());
  }

  @Test
  @DisplayName("should allow user to view their own history when userId matches")
  void shouldAllowUserToViewOwnHistory() {
    PixelHistory ph = new PixelHistory();
    ph.setCoordinateX(7);
    ph.setCoordinateY(8);
    ph.setNewColor("#FFAA00");

    when(pixelHistoryRepository.findAllByUserAfter(1L, 0L, 1000))
        .thenReturn(List.of(ph));
    CustomUserDetails user = new CustomUserDetails(1L, "user", "pass", "USER", 5);
    List<PixelHistoryDto> result =
        pixelHistoryService.getUserHistory(user, 1L, 0L, 1000);

    assertEquals(1, result.size());
    assertEquals("#FFAA00", result.get(0).getNewColor());
  }

  @Test
  @DisplayName("should throw UNAUTHORIZED when currentUser is null")
  void shouldThrowUnauthorizedWhenCurrentUserIsNull() {
    ApiException ex = assertThrows(
        ApiException.class,
        () -> pixelHistoryService.getUserHistory(null, null, 0L, 1000)
    );
    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
  }

  @Test
  @DisplayName("should throw FORBIDDEN when non-admin tries to view another user's history")
  void shouldThrowForbiddenForNonAdminAccess() {
    CustomUserDetails user = new CustomUserDetails(1L, "user", "pass", "USER", 5);
    ApiException ex = assertThrows(
        ApiException.class,
        () -> pixelHistoryService.getUserHistory(user, 2L, 0L, 1000)
    );
    assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
  }

  @Test
  @DisplayName("should allow admin to view any user's history")
  void shouldAllowAdminToViewOtherUserHistory() {
    PixelHistory ph = new PixelHistory();
    ph.setId(1L);
    ph.setCoordinateX(9);
    ph.setCoordinateY(9);
    ph.setNewColor("#00FF00");

    when(pixelHistoryRepository.findAllByUserAfter(2L, 0L, 1000))
        .thenReturn(List.of(ph));

    CustomUserDetails admin = new CustomUserDetails(1L, "admin", "pass", "ADMIN", 5);
    List<PixelHistoryDto> result =
        pixelHistoryService.getUserHistory(admin, 2L, 0L, 1000);

    assertEquals(1, result.size());
    assertEquals("#00FF00", result.get(0).getNewColor());
  }


  @Test
  @DisplayName("should throw exception when limit is negative")
  void shouldThrowExceptionWhenLimitIsNegative() {
    CustomUserDetails user = new CustomUserDetails(1L, "user", "pass", "USER", 5);
    ApiException ex = assertThrows(
        ApiException.class,
        () -> pixelHistoryService.getUserHistory(user, null, 0L, -5)
    );
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
  }

  @Test
  @DisplayName("should throw exception when fromId is negative")
  void shouldThrowExceptionWhenFromIdIsNegative() {
    CustomUserDetails user = new CustomUserDetails(1L, "user", "pass", "USER", 5);
    ApiException ex = assertThrows(
        ApiException.class,
        () -> pixelHistoryService.getUserHistory(user, null, -10L, 100)
    );
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
  }
}