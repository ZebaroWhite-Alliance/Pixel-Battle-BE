package ua.cn.stu.pixelbattle.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.cn.stu.pixelbattle.model.PixelHistory;


/**
 * Repository interface for managing {@link PixelHistory} entities.
 *
 * <p>Provides methods for retrieving pixel history records with pagination support.
 * Designed for efficient access to large datasets (e.g., millions of records)
 * by fetching data in small chunks instead of loading the entire history at once.
 */
@Repository
public interface PixelHistoryRepository extends JpaRepository<PixelHistory, Long> {

  /**
   * Retrieves a portion of the global pixel history starting after a specific record ID.
   *
   * <p>Intended for streaming or incremental loading (e.g., to animate board history).
   * Results are ordered by ID ascending and limited to the specified amount.
   *
   * @param fromId the ID of the last already retrieved record (exclusive)
   * @param limit  the maximum number of records to retrieve
   * @return a list of {@link PixelHistory} entries ordered by ID ascending
   */
  @Query(value = """
            SELECT * FROM pixel_history
      WHERE id > :fromId
      ORDER BY id ASC
      LIMIT :limit
      """, nativeQuery = true)
  List<PixelHistory> findAllAfter(
      @Param("fromId") long fromId,
      @Param("limit") int limit);

  /**
   * Retrieves a portion of pixel history for a specific user starting after a given record ID.
   *
   * <p>Useful for showing a particular user’s contributions or changes over time.
   * Results are ordered by ID ascending and limited to the specified amount.
   *
   * @param userId the ID of the user
   * @param fromId the ID of the last already retrieved record (exclusive)
   * @param limit  the maximum number of records to retrieve
   * @return a list of {@link PixelHistory} entries belonging to the specified user
   */
  @Query(value = """
      SELECT * FROM pixel_history
      WHERE user_id = :userId AND id > :fromId
      ORDER BY id ASC
      LIMIT :limit
      """, nativeQuery = true)
  List<PixelHistory> findAllByUserAfter(
      @Param("userId") long userId,
      @Param("fromId") long fromId,
      @Param("limit") int limit
  );


}