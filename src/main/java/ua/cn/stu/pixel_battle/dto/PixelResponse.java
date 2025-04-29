package ua.cn.stu.pixel_battle.dto;

public class PixelResponse {
    private int x;
    private int y;
    private String color;

    public PixelResponse(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getColor() {
        return color;
    }
}
