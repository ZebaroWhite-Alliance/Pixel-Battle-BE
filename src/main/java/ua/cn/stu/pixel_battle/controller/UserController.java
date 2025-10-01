package ua.cn.stu.pixel_battle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixel_battle.dto.PixelHistoryDTO;
import ua.cn.stu.pixel_battle.dto.UserResponse;
import ua.cn.stu.pixel_battle.model.PixelHistory;
import ua.cn.stu.pixel_battle.security.CustomUserDetails;
import ua.cn.stu.pixel_battle.service.PixelHistoryService;
import ua.cn.stu.pixel_battle.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/user")

public class UserController {
    private final UserService userService;
    private final PixelHistoryService pixelHistoryService;

    public UserController(UserService userService, PixelHistoryService pixelHistoryService) {
        this.userService = userService;
        this.pixelHistoryService = pixelHistoryService;
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            UserResponse response = new UserResponse( userDetails.getId(),userDetails.getUsername(), userDetails.getPixelChangesCount());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user authenticated");
    }


    @GetMapping("/history")
    public ResponseEntity<?> getPixelHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            List<PixelHistoryDTO> history = pixelHistoryService.getHistoryByUserId(userDetails.getId());
            if (history.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pixel history found for this user.");
            }
            return ResponseEntity.ok(history);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user authenticated");
    }
}
