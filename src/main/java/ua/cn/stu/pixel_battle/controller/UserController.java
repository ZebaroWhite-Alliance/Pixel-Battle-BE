package ua.cn.stu.pixel_battle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixel_battle.dto.UserResponse;
import ua.cn.stu.pixel_battle.security.CustomUserDetails;
import ua.cn.stu.pixel_battle.service.UserService;


@RestController
@RequestMapping("/pixel-battle/api/v1/user")

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            UserResponse response = new UserResponse( userDetails.getId(),userDetails.getUsername(), userDetails.getPixelChangesCount());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user authenticated");
    }
}
