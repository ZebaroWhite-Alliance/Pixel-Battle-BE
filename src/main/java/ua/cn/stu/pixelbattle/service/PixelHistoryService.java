package ua.cn.stu.pixelbattle.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.dto.PixelHistoryDto;
import ua.cn.stu.pixelbattle.model.PixelHistory;
import ua.cn.stu.pixelbattle.repository.PixelHistoryRepository;



/**
 * Service for managing pixel history records.
 *
 * <p>Provides methods to:
 * <ul>
 *     <li>Retrieve all pixel changes after a specific ID</li>
 *     <li>Retrieve the next pixel change after a specific ID</li>
 *     <li>Retrieve pixel history for a specific user as DTOs</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PixelHistoryService {

  private final PixelHistoryRepository repository;

  // OPTIMIZE - add pagination
  /**
   * Retrieves all pixel history entries with ID greater than the specified value.
   *
   * @param id the ID to compare
   * @return list of {@link PixelHistory} entries ordered by ID ascending
   */
  public List<PixelHistory> getAllAfterId(Long id) {
    return repository.findByIdGreaterThanOrderByIdAsc(id);
  }

  /**
   * Retrieves the next pixel history entry with ID greater than the specified value.
   *
   * @param id the ID to compare
   * @return an {@link Optional} containing the next {@link PixelHistory}, if exists
   */
  public Optional<PixelHistory> getNextAfterId(Long id) {
    return repository.findByIdGreaterThanOrderByIdAsc(id).stream().findFirst();
  }

  /**
   * Retrieves pixel history for a specific user and converts it to DTOs.
   *
   * @param userId the ID of the user
   * @return list of {@link PixelHistoryDto} representing the user's pixel changes
   */
  public List<PixelHistoryDto> getHistoryByUserId(Long userId) {
    List<PixelHistory> history = repository.findByUserId(userId);
    return history.stream()
        .map(h -> new PixelHistoryDto(
            h.getId(),
            h.getCoordinateX(),
            h.getCoordinateY(),
            h.getNewColor()

        ))
        .collect(Collectors.toList());
  }
}

