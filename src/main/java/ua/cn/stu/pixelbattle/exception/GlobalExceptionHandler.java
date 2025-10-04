package ua.cn.stu.pixelbattle.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application.
 *
 * <p>Handles common exceptions and returns a structured JSON response
 * containing timestamp, HTTP status, error type, and message.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles {@link IllegalArgumentException} exceptions.
   *
   * @param ex the exception thrown
   * @return ResponseEntity with status 400 and a structured error body
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Bad Request");
    body.put("message", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles {@link RateLimitException} exceptions.
   *
   * @param ex the exception thrown
   * @return ResponseEntity with status 429 and a structured error body
   */
  @ExceptionHandler(RateLimitException.class)
  public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
    body.put("error", "Too Many Requests");
    body.put("message", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.TOO_MANY_REQUESTS);
  }

  /**
   * Handles all other uncaught exceptions.
   *
   * @param ex the exception thrown
   * @return ResponseEntity with status 500 and a structured error body
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "Internal Server Error");
    body.put("message", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
