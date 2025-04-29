package ua.cn.stu.pixel_battle.dto;

public class UserResponse {
    private Long userId;
    private String username;



    public UserResponse(Long userId, String username) {
        this.userId = userId;
        this.username = username;


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

}
