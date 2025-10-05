package ua.cn.stu.pixelbattle.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixelbattle.dto.UserSessionResponse;
import ua.cn.stu.pixelbattle.service.SessionService;

/**
 * REST controller for managing the current user session.
 *
 * <p>The {@code /session} endpoint allows the frontend to check if a user is
 * logged in using an HTTP-only refresh token. Returns minimal user information
 * (id and username) if the session is valid.
 */
@RestController
@RequestMapping(("/api/v1"))
@AllArgsConstructor
public class SessionController {

  private final SessionService sessionService;

  /**
   * Retrieves the current user's session information.
   *
   * <p>This method checks the refresh token from the HTTP-only cookie. If the token is valid,
   * it returns a {@link UserSessionResponse} containing the user's id and username.
   * If the session is invalid or the user is not found, it returns a 401 Unauthorized status.</p>
   *
   * @param request the {@link HttpServletRequest} containing cookies
   * @return a {@link ResponseEntity} with {@link UserSessionResponse} or 401 status
   */
  @GetMapping("/session")
  public ResponseEntity<UserSessionResponse> getSession(HttpServletRequest request) {
    try {
      UserSessionResponse response = sessionService.getSessionResponse(request);
      if (response == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      return ResponseEntity.ok(response);
    } catch (Exception ex) {
      // service may throw when token invalid/expired — treat as unauthorized
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }
}
