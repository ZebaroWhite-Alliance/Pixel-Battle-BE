package ua.cn.stu.pixelbattle.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global exception handler for the application.
 *
 * <p>Handles common exceptions and returns a structured JSON response
 * containing timestamp, HTTP status, error type, and message.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles {@link ApiException}, allowing custom HTTP status and message.
   */
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", ex.getStatus().value());
    body.put("error", ex.getStatus().getReasonPhrase());
    body.put("message", ex.getMessage());
    return new ResponseEntity<>(body, ex.getStatus());
  }

  /**
   * Handles {@link IllegalArgumentException} exceptions.
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
   * Handles {@link MethodArgumentNotValidException} exceptions triggered by
   * Bean Validation failures (e.g., @NotBlank, @Pattern, @Size).
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
   * Handles request validation errors.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolation(
      ConstraintViolationException ex) {
    Map<String, String> fieldErrors = new HashMap<>();

    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage());
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
   * Handles case when user is not found.
   */
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.NOT_FOUND.value()); // 404
    body.put("error", "Not Found");
    body.put("message", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  /** Handles forbidden actions (e.g., user trying to delete another user’s resource). */
  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException ex) {
    return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  /** Handles missing authentication (null user in @AuthenticationPrincipal). */
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException ex) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication required");
  }

  /** Passes through ResponseStatusException with its original HTTP code. */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
    return buildResponse(status, ex.getReason());
  }

  /**
   * Handles all uncaught exceptions and returns a 500 Internal Server Error response.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected failure");
  }

  // Utility method to build a consistent JSON body
  private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    return new ResponseEntity<>(body, status);
  }
}