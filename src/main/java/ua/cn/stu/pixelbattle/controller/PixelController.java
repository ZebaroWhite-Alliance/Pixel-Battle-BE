package ua.cn.stu.pixelbattle.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixelbattle.dto.PixelChangeRequest;
import ua.cn.stu.pixelbattle.dto.PixelResponse;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;
import ua.cn.stu.pixelbattle.service.PixelService;

/**
 * Controller for pixel-related operations.
 *
 * <p>Provides endpoints to retrieve pixel board data and update pixels.
 */
@RestController
@RequestMapping("/api/v1/pixels")
@RequiredArgsConstructor
public class PixelController {

  private final PixelService pixelService;

  /**
   * Retrieves all pixels from the pixel board.
   *
   * @return list of {@link PixelResponse} representing current pixel state
   */
  @GetMapping
  public List<PixelResponse> getAllPixels() {
    return pixelService.getAllPixels();
  }


  /**
   * Changes the color of a specific pixel.
   *
   * @param request pixel change request containing coordinates and color
   * @param user    the authenticated user making the change
   * @return HTTP 200 OK if the pixel was successfully updated
   */
  @PostMapping
  public ResponseEntity<Void> changePixel(@Valid  @RequestBody PixelChangeRequest request,
                                          @AuthenticationPrincipal CustomUserDetails user) {

    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    pixelService.changePixel(
        request.getCoordinateX(),
        request.getCoordinateY(),
        request.getColor(),
        user.getId());
    return ResponseEntity.ok().build();
  }
}
