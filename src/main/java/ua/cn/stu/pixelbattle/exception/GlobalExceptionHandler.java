package ua.cn.stu.pixelbattle.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
   * Handles {@link MethodArgumentNotValidException} exceptions triggered by
   * Bean Validation failures (e.g., @NotBlank, @Pattern, @Size).
   *
   * <p>Returns a structured JSON with a map of field-specific errors.
   *
   * @param ex the thrown MethodArgumentNotValidException
   * @return ResponseEntity with HTTP 400 and structured error body including field errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Bad Request");
    body.put("message", "Validation failed");
    body.put("fields", fieldErrors);

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles invalid credentials during login.
   * Returns HTTP 401 with a JSON containing timestamp, status, error, and message.
   *
   * @param ex the thrown BadCredentialsException
   * @return ResponseEntity with HTTP 401
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.UNAUTHORIZED.value()); // 401
    body.put("error", "Unauthorized");
    body.put("message", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
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
