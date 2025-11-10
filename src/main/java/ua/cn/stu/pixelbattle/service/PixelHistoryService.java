package ua.cn.stu.pixelbattle.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.cn.stu.pixelbattle.dto.PixelHistoryDto;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.model.PixelHistory;
import ua.cn.stu.pixelbattle.repository.PixelHistoryRepository;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;


/**
 * Service for managing pixel history records.
 *
 * <p>Provides methods to retrieve pixel changes globally or for a specific user.
 * Includes access control for non-admin users to prevent viewing other users' history.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PixelHistoryService {

  private final PixelHistoryRepository pixelHistoryRepository;
  private static final int MAX_LIMIT = 10_000;

  /**
   * Retrieves a list of pixel history records with IDs greater than {@code fromId}.
   *
   * @param fromId the starting pixel history ID (exclusive)
   * @param limit the maximum number of records to fetch
   * @return a list of {@link PixelHistoryDto} objects representing the pixel changes
   */
  public List<PixelHistoryDto> getAllAfter(long fromId, int limit) {
    int safeLimit = Math.min(limit, MAX_LIMIT);
    List<PixelHistory> entities = pixelHistoryRepository.findAllAfter(fromId, safeLimit);
    return entities.stream().map(this::toDto).toList();
  }

  /**
   * Retrieves pixel history records for a specific user.
   *
   * <p>If {@code userId} is {@code null}, retrieves history for the current user.
   * Non-admin users cannot access history of other users.</p>
   *
   * @param currentUser the currently authenticated user; must not be {@code null}
   * @param userId the ID of the target user, or {@code null} to fetch current user's history
   * @param fromId the starting pixel history ID (exclusive)
   * @param limit the maximum number of records to fetch
   * @return a list of {@link PixelHistoryDto} objects representing the pixel changes
   * @throws SecurityException if {@code currentUser} is {@code null}
   * @throws ApiException if a non-admin user attempts to access another user's history
   */
  public List<PixelHistoryDto> getUserHistory(
      CustomUserDetails currentUser,
      Long userId, // can be null for ‘myself’
      long fromId,
      int limit
  ) {
    if (currentUser == null) {
      throw new ApiException("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());

    if (!isAdmin && userId != null && !userId.equals(currentUser.getId())) {
      throw new ApiException(
          "Access denied: only admins can view other users history", HttpStatus.FORBIDDEN);
    }

    Long targetUserId = (userId != null) ? userId : currentUser.getId();

    int safeLimit = Math.min(limit, MAX_LIMIT);
    List<PixelHistory> entities =
        pixelHistoryRepository.findAllByUserAfter(targetUserId, fromId, safeLimit);
    return entities
        .stream()
        .map(this::toDto)
        .toList();
  }


  private PixelHistoryDto toDto(PixelHistory pixelHistory) {
    return new PixelHistoryDto(
        pixelHistory.getId(),
        pixelHistory.getCoordinateX(),
        pixelHistory.getCoordinateY(),
        pixelHistory.getNewColor()
    );
  }
}

