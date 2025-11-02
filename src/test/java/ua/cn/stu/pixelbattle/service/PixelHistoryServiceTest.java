package ua.cn.stu.pixelbattle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import ua.cn.stu.pixelbattle.dto.PixelHistoryDto;
import ua.cn.stu.pixelbattle.model.PixelHistory;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.PixelHistoryRepository;

/**
 * Unit tests for {@link PixelHistoryService}.
 *
 * <p>Covers methods:
 * <ul>
 *   <li>{@link PixelHistoryService#getAllAfterId(Long)}</li>
 *   <li>{@link PixelHistoryService#getNextAfterId(Long)}</li>
 *   <li>{@link PixelHistoryService#getHistoryByUserId(Long)}</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class PixelHistoryServiceTest {

  @Mock
  private PixelHistoryRepository pixelHistoryRepository;

  @InjectMocks
  private PixelHistoryService pixelHistoryService;

  @Test
  @DisplayName("should return all pixels after id")
  void getAllAfterId() {
    User  user = new User();
    user.setId(1L);
    user.setUsername("user");

    PixelHistory pixelHistory1 = new PixelHistory(0, 0, "#FFF000", "000FFF", user);
    PixelHistory pixelHistory2 = new PixelHistory(1, 1, "#FFF000", "000FFF", user);

    List<PixelHistory> list = List.of(pixelHistory1, pixelHistory2);
    when(pixelHistoryRepository.findByIdGreaterThanOrderByIdAsc(0L)).thenReturn(list);


    List<PixelHistory> result = pixelHistoryService.getAllAfterId(0L);


    assertEquals(2, result.size());
    assertEquals(0, result.get(0).getCoordinateX());
    assertEquals(1, result.get(1).getCoordinateX());
  }

  @Test
  @DisplayName("should return next pixel after id")
  void getNextAfterId() {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");

    PixelHistory p1 = new PixelHistory(2, 2, "#AAA000", "000AAA", user);
    PixelHistory p2 = new PixelHistory(3, 3, "#BBB000", "000BBB", user);

    List<PixelHistory> list = List.of(p1, p2);
    when(pixelHistoryRepository.findByIdGreaterThanOrderByIdAsc(1L)).thenReturn(list);

    Optional<PixelHistory> next = pixelHistoryService.getNextAfterId(1L);

    assertTrue(next.isPresent());
    assertEquals(2, next.get().getCoordinateX());
  }

  @Test
  @DisplayName("should return empty optional when no next pixel")
  void getNextAfterId_empty() {
    when(pixelHistoryRepository.findByIdGreaterThanOrderByIdAsc(10L)).thenReturn(List.of());

    Optional<PixelHistory> next = pixelHistoryService.getNextAfterId(10L);

    assertTrue(next.isEmpty());
  }

  @Test
  @DisplayName("should return pixel history DTOs for a user")
  void getHistoryByUserId() {
    User user = new User();
    user.setId(1L);

    PixelHistory p1 = new PixelHistory(1, 5, "#FFF000", "#FF0000", user);
    PixelHistory p2 = new PixelHistory(10, 10, "#FFF000", "#00FF00", user);

    List<PixelHistory> list = List.of(p1, p2);
    when(pixelHistoryRepository.findByUserId(1L)).thenReturn(list);

    List<PixelHistoryDto> dtos = pixelHistoryService.getHistoryByUserId(1L);

    assertEquals(2, dtos.size());
    assertEquals("#FF0000", dtos.get(0).getNewColor());
    assertEquals(10, dtos.get(1).getCoordinateX());
  }



}
