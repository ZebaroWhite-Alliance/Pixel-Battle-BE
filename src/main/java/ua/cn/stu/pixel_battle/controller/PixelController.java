package ua.cn.stu.pixel_battle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.cn.stu.pixel_battle.dto.PixelChangeRequest;
import ua.cn.stu.pixel_battle.dto.PixelResponse;
import ua.cn.stu.pixel_battle.security.CustomUserDetails;
import ua.cn.stu.pixel_battle.service.JWTTokenService;
import ua.cn.stu.pixel_battle.service.PixelService;
import ua.cn.stu.pixel_battle.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/pixel")
public class PixelController {

    private final PixelService pixelService;
    private final JWTTokenService jwtTokenService;

    @Autowired
    public PixelController(PixelService pixelService, JWTTokenService jwtTokenService) {
        this.pixelService = pixelService;
        this.jwtTokenService = jwtTokenService;
    }
    @GetMapping
    public List<PixelResponse> getAllPixels() {
        return pixelService.getAllPixels();
    }
    @PostMapping("/pixel-battle/api/v1/change")
    public ResponseEntity<Void> changePixel(@RequestBody PixelChangeRequest request,
                                            @AuthenticationPrincipal CustomUserDetails user) {
        pixelService.changePixel(request.getX(), request.getY(), request.getColor(), user.getId());
        return ResponseEntity.ok().build();
    }
}
