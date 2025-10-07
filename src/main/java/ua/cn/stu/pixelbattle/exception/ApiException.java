package ua.cn.stu.pixelbattle.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for API errors.
 * Allows specifying an HTTP status code along with the message.
 */
public class ApiException extends RuntimeException {

  private final HttpStatus status;

  /**
   * Constructs a new ApiException with a message and HTTP status.
   *
   * @param message error message
   * @param status HTTP status code
   */
  public ApiException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  /** Returns the HTTP status associated with this exception. */
  public HttpStatus getStatus() {
    return status;
  }
}
