package ua.cn.stu.pixelbattle.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.cn.stu.pixelbattle.model.PixelHistory;


/**
 * Repository interface for {@link PixelHistory} entities.
 *
 * <p>Provides CRUD operations and custom query methods for accessing pixel history.
 */
@Repository
public interface PixelHistoryRepository extends JpaRepository<PixelHistory, Long> {

  /**
   * Retrieves all pixel history entries for a specific user.
   *
   * @param userId the ID of the user
   * @return list of {@link PixelHistory} objects for the given user
   */
  List<PixelHistory> findByUserId(Long userId);

  /**
   * Retrieves all pixel history entries ordered by ID in ascending order.
   *
   * @return list of all {@link PixelHistory} objects sorted by ID ascending
   */
  List<PixelHistory> findAllByOrderByIdAsc();

  /**
   * Retrieves all pixel history entries with ID greater than the specified value,
   * ordered by ID in ascending order.
   *
   * @param id the ID threshold
   * @return list of {@link PixelHistory} objects with ID greater than the specified value
   */
  List<PixelHistory> findByIdGreaterThanOrderByIdAsc(Long id);

}