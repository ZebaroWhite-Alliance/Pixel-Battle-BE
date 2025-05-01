package ua.cn.stu.pixel_battle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.cn.stu.pixel_battle.dto.PixelChangeRequest;
import ua.cn.stu.pixel_battle.dto.PixelResponse;
import ua.cn.stu.pixel_battle.security.CustomUserDetails;
import ua.cn.stu.pixel_battle.service.PixelService;

import java.util.List;

@RestController
@RequestMapping("/pixel-battle/api/v1/pixel")
public class PixelController {

    private final PixelService pixelService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PixelController(PixelService pixelService, SimpMessagingTemplate messagingTemplate) {
        this.pixelService = pixelService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public List<PixelResponse> getAllPixels() {
        return pixelService.getAllPixels();
    }

    @PostMapping("/change")
    public ResponseEntity<Void> changePixel(@RequestBody PixelChangeRequest request,
                                            @AuthenticationPrincipal CustomUserDetails user) {
        pixelService.changePixel(request.getX(), request.getY(), request.getColor(), user.getId());

        PixelResponse response = new PixelResponse(
                request.getX(),
                request.getY(),
                request.getColor(),
                user.getUsername()
        );
        System.out.println("Pixel updated via REST: x=" + request.getX()
                + ", y=" + request.getY()
                + ", color=" + request.getColor()
                + ", user=" + user.getUsername());
        messagingTemplate.convertAndSend("/topic/pixels", response);

        return ResponseEntity.ok().build();
    }
}
