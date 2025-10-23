package ua.cn.stu.pixelbattle.controller;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixelbattle.dto.PixelResponse;
import ua.cn.stu.pixelbattle.model.PixelHistory;
import ua.cn.stu.pixelbattle.service.PixelHistoryService;


/**
 * REST controller for retrieving pixel history data.
 *
 * <p>Provides endpoints to fetch pixel changes based on history IDs.
 */
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
@Validated
public class PixelHistoryController {

  private final PixelHistoryService service;

  /**
   * Retrieves all pixel changes that occurred after a specific history entry ID.
   *
   * @param id the ID after which changes should be retrieved
   * @return a list of {@link PixelResponse} representing pixel updates
   */
  @GetMapping("/after/{id}")
  public List<PixelResponse> getAllAfter(@PathVariable @Min(0) Long id) {
    return service.getAllAfterId(id).stream()
        .map(this::mapToResponse)
        .toList();
  }

  /**
   * Retrieves the next pixel change after the given history entry ID.
   *
   * @param id the history ID to search after
   * @return a {@link ResponseEntity} containing the next {@link PixelResponse}
   *     or {@link ResponseEntity#notFound()} if no entry exists
   */
  @GetMapping("/next/{id}")
  public ResponseEntity<PixelResponse> getNext(@PathVariable Long id) {
    return service.getNextAfterId(id)
        .map(this::mapToResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Maps a {@link PixelHistory} entity to a {@link PixelResponse}.
   *
   * @param history the pixel history entry
   * @return a mapped {@link PixelResponse}
   */
  private PixelResponse mapToResponse(PixelHistory history) {
    return new PixelResponse(
        history.getCoordinateX(),
        history.getCoordinateY(),
        history.getNewColor()
    );
  }
}
