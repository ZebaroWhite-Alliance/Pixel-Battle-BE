package ua.cn.stu.pixelbattle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


/**
 * Represents a user in the Pixel Battle system.
 *
 * <p>Contains username, password hash, role, creation timestamp, and count of pixel changes.
 * JPA entity mapped to the "users" table.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username", nullable = false, unique = true, length = 100)
  private String username;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "created_at", nullable = false, updatable = false)
  @org.hibernate.annotations.CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "role", nullable = false)
  private String role = "USER";

  @Column(name = "pixel_changes_count", nullable = false)
  private int pixelChangesCount = 0;

  /**
   * Increments the pixel changes count on 1.
   */
  public void incrementPixelChanges() {
    this.pixelChangesCount++;
  }
}
