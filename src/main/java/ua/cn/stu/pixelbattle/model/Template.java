package ua.cn.stu.pixelbattle.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.Type;
import ua.cn.stu.pixelbattle.dto.PixelResponse;


/**
 * Entity representing a saved pixel art template.
 *
 * <p>Each template belongs to a specific user and stores a list of pixels
 * (coordinates and colors) in JSONB format within the database.</p>
 *
 * <p>The {@code createdAt} timestamp is automatically set upon creation,
 * and a default name is generated if none is provided.</p>
 */
@Entity
@Data
@Table(name = "template")
public class Template {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<PixelResponse> pixels;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  /**
   * Automatically sets a default name for the template before persisting,
   * if no name has been provided by the user.
   *
   * <p>The default name is based on the template ID (if available)
   * or the current system timestamp.</p>
   */
  @PrePersist
  public void setDefaultNameIfEmpty() {
    if (name == null || name.isBlank()) {
      this.name = String.valueOf(id != null ? id : System.currentTimeMillis());
    }
  }
}