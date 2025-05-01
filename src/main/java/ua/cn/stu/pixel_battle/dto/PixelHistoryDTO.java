package ua.cn.stu.pixel_battle.dto;

public class PixelHistoryDTO {
    private Long id;
    private int x;
    private int y;
    private String newColor;

    public PixelHistoryDTO(Long id, int x, int y, String newColor) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.newColor = newColor;
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

    public String getNewColor() {
        return newColor;
    }

    public void setNewColor(String newColor) {
        this.newColor = newColor;
    }
}
