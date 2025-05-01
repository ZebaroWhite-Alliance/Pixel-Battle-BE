package ua.cn.stu.pixel_battle.dto;

public class PixelChangeRequest {
    private int x;
    private int y;
    private String color;
    private Long userId;
    private String username;

    public PixelChangeRequest() {
    }

    public PixelChangeRequest(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
        System.out.println("PixelChangeRequest" + x + " " + y + " " + color );
    }

    public PixelChangeRequest(int x, int y, String color, Long userId, String username) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.userId = userId;
        this.username = username;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
