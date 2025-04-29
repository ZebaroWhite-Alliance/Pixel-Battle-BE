package ua.cn.stu.pixel_battle.dto;

public class UserResponse {
    private Long userId;
    private String username;
    private int pixelChangesCount;


    public UserResponse(Long userId, String username, int pixelChangesCount) {
        this.userId = userId;
        this.username = username;
        this.pixelChangesCount = pixelChangesCount;


    }

    public String getUsername() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }



    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getPixelChangesCount() {
        return pixelChangesCount;
    }

    public void setPixelChangesCount(int pixelChangesCount) {
        this.pixelChangesCount = pixelChangesCount;
    }
}
