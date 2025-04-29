package ua.cn.stu.pixel_battle.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PixelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int x;
    private int y;
    private String oldColor;
    private String newColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime changedAt;

    public PixelHistory(int x, int y, String oldColor, String newColor, User user) {
        this.x = x;
        this.y = y;
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.user = user;
        this.changedAt = LocalDateTime.now();
    }

    public PixelHistory() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getOldColor() {
        return oldColor;
    }

    public void setOldColor(String oldColor) {
        this.oldColor = oldColor;
    }

    public String getNewColor() {
        return newColor;
    }

    public void setNewColor(String newColor) {
        this.newColor = newColor;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
