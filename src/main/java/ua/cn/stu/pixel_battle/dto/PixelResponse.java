package ua.cn.stu.pixel_battle.dto;

public class PixelResponse {
    private int x;
    private int y;
    private String color;
    private String username;

    public PixelResponse(int x, int y, String color,String username) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.username = username;
        System.out.println(" PixelResponse"  + x + " " + y + " " + color + " " + username );
    }

    public String getUsername() {
        return username;
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
