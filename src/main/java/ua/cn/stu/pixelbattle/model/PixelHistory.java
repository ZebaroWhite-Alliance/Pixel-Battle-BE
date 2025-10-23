package ua.cn.stu.pixelbattle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity representing a historical change of a pixel.
 *
 * <p>Stores the coordinates of the pixel, the old and new colors,
 * the user who made the change, and the timestamp of the change.</p>
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "pixel_history")
public class PixelHistory {

  /**
   * Constructs a PixelHistory with coordinates, old and new color, and the user.
   * The ID and timestamp will be set automatically by JPA/Hibernate.
   *
   * @param coordinateX        the X coordinate of the pixel
   * @param coordinateY        the Y coordinate of the pixel
   * @param oldColor the previous color of the pixel
   * @param newColor the new color of the pixel
   * @param user     the user who made the change
   */
  public PixelHistory(
      int coordinateX,
      int coordinateY,
      String oldColor,
      String newColor,
      User user) {
    this.coordinateX = coordinateX;
    this.coordinateY = coordinateY;
    this.oldColor = oldColor;
    this.newColor = newColor;
    this.user = user;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private int coordinateX;
  @Column(nullable = false)
  private int coordinateY;
  private String oldColor;

  @Column(name = "new_color", length = 7)
  private String newColor;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "changed_at", nullable = false, updatable = false)
  @org.hibernate.annotations.CreationTimestamp
  private LocalDateTime changedAt;

}
