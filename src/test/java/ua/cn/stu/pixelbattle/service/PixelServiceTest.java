package ua.cn.stu.pixelbattle.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ua.cn.stu.pixelbattle.config.GameProperties;
import ua.cn.stu.pixelbattle.dto.GameInfoResponse;
import ua.cn.stu.pixelbattle.dto.PixelResponse;
import ua.cn.stu.pixelbattle.exception.ApiException;
import ua.cn.stu.pixelbattle.model.Pixel;
import ua.cn.stu.pixelbattle.model.PixelHistory;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.PixelHistoryRepository;
import ua.cn.stu.pixelbattle.repository.UserRepository;

/**
 * Unit tests for {@link PixelService}.
 *
 * <p>Verifies main service scenarios:
 * <ul>
 *   <li>Fetching pixels from Redis</li>
 *   <li>Changing pixel colors by users and admins</li>
 *   <li>Cooldown (rate limit) behavior</li>
 *   <li>Retrieving all pixels and game info</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class PixelServiceTest {
  @Mock
  UserRepository userRepository;

  @Mock
  private RedisTemplate<String, Pixel> redisTemplate;

  @Mock
  private ValueOperations<String, Pixel> valueOperations;

  @Mock
  private StringRedisTemplate stringRedisTemplate;

  @Mock
  private ValueOperations<String, String> stringValueOperations;

  @Mock
  PixelHistoryRepository pixelHistoryRepository;

  @Mock
  private GameProperties gameProperties;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  PixelService pixelService;


  @BeforeEach
  void setUp() {
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(stringRedisTemplate.opsForValue()).thenReturn(stringValueOperations);

    when(gameProperties.getWidth()).thenReturn(100);
    when(gameProperties.getHeight()).thenReturn(100);
    when(gameProperties.getCooldown()).thenReturn(10);

    pixelService = new PixelService(
        redisTemplate,
        stringRedisTemplate,
        pixelHistoryRepository,
        userRepository,
        messagingTemplate,
        gameProperties
    );
  }

  @AfterEach
  void tearDown() {
    reset(userRepository, redisTemplate, stringRedisTemplate,
        pixelHistoryRepository, gameProperties, messagingTemplate);
  }


  // -------------------GET PIXEL----------------------------------
  @Test
  @DisplayName("should return pixel when found")
  void shouldReturnPixelWhenFound() {

    Pixel pixel = new Pixel(1, 2, "#FF0000", "user");
    when(valueOperations.get("pixel:1:2")).thenReturn(pixel);
    Pixel result = pixelService.getPixel(1, 2);

    assertNotNull(result);
    assertEquals(1, result.getCoordinateX());
    assertEquals(2, result.getCoordinateY());
    assertEquals("#FF0000", result.getColor());
    assertEquals("user", result.getUsername());
  }

  @Test
  @DisplayName("should return null when pixel not found")
  void shouldReturnNullWhenPixelNotFound() {
    when(valueOperations.get("pixel:1:2")).thenReturn(null);

    Pixel result = pixelService.getPixel(1, 2);
    assertNull(result);
  }

  // -------------------------CHANGE PIXEL--------------------------

  @Test
  @DisplayName("should throw when coordinates out of bounds")
  void shouldThrowWhenCoordinatesOutOfBounds() {
    assertThrows(IllegalArgumentException.class, () ->
        pixelService.changePixel(-1, -1, "#FFF000", 1L)
    );
  }

  @Test
  @DisplayName("should throw when user not found")
  void shouldThrowWhenUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> pixelService.changePixel(1, 1, "#FFF000", 1L));
    assertEquals("User not found: 1", ex.getMessage());

  }

  @Test
  @DisplayName("should do nothing when color is the same")
  void shouldDoNothingWhenSameColor() {
    User user = new User();
    user.setUsername("user");
    user.setId(1L);
    user.setRole("ADMIN");

    Pixel pixel = new Pixel(1, 2, "#FFF000", "userTest");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(valueOperations.get("pixel:1:2")).thenReturn(pixel);

    pixelService.changePixel(1, 2, "#FFF000", 1L);


    verify(pixelHistoryRepository, never()).save(any());
    verify(userRepository, never()).save(any());
    verify(valueOperations, never()).set(anyString(), any(Pixel.class));

  }

  @Test
  @DisplayName("should change pixel successfully")
  void shouldChangePixelSuccessfully() {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setRole("USER");

    Pixel oldPixel = new Pixel(1, 2, "#FFFFFF", "user");

    // configs
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    // redis
    when(valueOperations.get("pixel:1:2")).thenReturn(oldPixel);
    doNothing().when(valueOperations).set(anyString(), any(Pixel.class));


    when(stringValueOperations.setIfAbsent(
        anyString(),
        anyString(),
        anyLong(),
        any()
    )).thenReturn(true);

    pixelService.changePixel(1, 2, "#FF0000", 1L);


    verify(valueOperations).set("pixel:1:2", new Pixel(1, 2, "#FF0000", "user"));
    verify(pixelHistoryRepository).save(any(PixelHistory.class));
    verify(userRepository).save(user);
    verify(messagingTemplate).convertAndSend(eq("/topic/pixels"), any(Object.class));

  }

  @Test
  @DisplayName("should throw when cooldown not expired")
  void shouldThrowWhenCooldownNotExpired() {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setRole("USER");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(stringValueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
        .thenReturn(false);

    ApiException ex = assertThrows(ApiException.class, () ->
        pixelService.changePixel(1, 2, "#FF0000", 1L)
    );

    assertEquals(HttpStatus.TOO_MANY_REQUESTS, ex.getStatus());
  }

  @Test
  @DisplayName("should change pixel when old pixel is null")
  void shouldChangePixelWhenOldPixelIsNull() {
    User user = new User();
    user.setId(1L);
    user.setUsername("user");
    user.setRole("USER");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(valueOperations.get("pixel:5:5")).thenReturn(null);
    when(stringValueOperations.setIfAbsent(
        anyString(),
        anyString(),
        anyLong(),
        any()))
        .thenReturn(true);

    pixelService.changePixel(5, 5, "#123456", 1L);

    verify(pixelHistoryRepository).save(any(PixelHistory.class));
    verify(valueOperations).set(eq("pixel:5:5"), any(Pixel.class));
    verify(userRepository).save(user);
    verify(messagingTemplate).convertAndSend(eq("/topic/pixels"), any(PixelResponse.class));
  }

  @Test
  @DisplayName("should allow admin to change pixel without cooldown")
  void shouldAllowAdminToChangePixelWithoutCooldown() {
    User admin = new User();
    admin.setId(1L);
    admin.setUsername("adminUser");
    admin.setRole("ADMIN");

    Pixel oldPixel = new Pixel(1, 2, "#AAAAAA", "someone");

    when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
    when(valueOperations.get("pixel:1:2")).thenReturn(oldPixel);

    pixelService.changePixel(1, 2, "#BBBBBB", 1L);

    verify(stringValueOperations, never()).setIfAbsent(anyString(), anyString(), anyLong(), any());
    verify(valueOperations).set(eq("pixel:1:2"), any(Pixel.class));
    verify(pixelHistoryRepository).save(any(PixelHistory.class));
    verify(userRepository).save(admin);
    verify(messagingTemplate).convertAndSend(eq("/topic/pixels"), any(PixelResponse.class));
  }


  // -------------------GET ALL PIXELS----------------------------------

  @Test
  @DisplayName("should return empty list when no pixels exist")
  void shouldReturnEmptyListWhenNoPixelsExist() {
    when(redisTemplate.keys("pixel:*")).thenReturn(Collections.emptySet());

    var result = pixelService.getAllPixels();

    assertNotNull(result);
    assertEquals(0, result.size());
    verify(redisTemplate).keys("pixel:*");
    verify(redisTemplate, never()).opsForValue();
  }


  @Test
  @DisplayName("should return list of pixels when data exists")
  void shouldReturnListOfPixelsWhenDataExists() {
    Set<String> keys = Set.of("pixel:1:1", "pixel:2:2");

    Pixel pixel1 = new Pixel(1, 1, "#FFFFFF", "user1");
    Pixel pixel2 = new Pixel(2, 2, "#000000", "user2");

    when(redisTemplate.keys("pixel:*")).thenReturn(keys);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.multiGet(keys)).thenReturn(List.of(pixel1, pixel2));

    List<PixelResponse> result = pixelService.getAllPixels();

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(redisTemplate).keys("pixel:*");
    verify(valueOperations).multiGet(keys);
  }

  @Test
  @DisplayName("should throw RuntimeException on Redis error")
  void shouldThrowRuntimeExceptionOnRedisError() {
    when(redisTemplate.keys("pixel:*")).thenThrow(new RuntimeException("Redis error"));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> pixelService.getAllPixels());

    assertEquals("Unable to connect to Redis: Redis error", ex.getMessage());
    verify(redisTemplate).keys("pixel:*");
  }


  // -------------------GET GAME INFO----------------------------------

  @Test
  @DisplayName("should return game info successfully")
  void shouldReturnGameInfoSuccessfully() {
    when(gameProperties.getWidth()).thenReturn(200);
    when(gameProperties.getHeight()).thenReturn(300);
    when(gameProperties.getCooldown()).thenReturn(15);

    GameInfoResponse result = pixelService.getGameInfo();

    assertNotNull(result);
    assertEquals(200, result.getWidth());
    assertEquals(300, result.getHeight());
    assertEquals(15, result.getCooldown());
  }

}
