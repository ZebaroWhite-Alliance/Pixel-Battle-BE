package ua.cn.stu.pixelbattle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a client exceeds the allowed rate limit.
 *
 * <p>This exception is mapped to HTTP status {@link HttpStatus#TOO_MANY_REQUESTS} (429)
 * and can be handled by a global exception handler to return a structured response.</p>
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitException extends RuntimeException {

  /**
   * Constructs a new {@code RateLimitException} with the specified detail message.
   *
   * @param message the detail message explaining the reason for the exception
   */
  public RateLimitException(String message) {
    super(message);
  }
}