package ua.cn.stu.pixel_battle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ua.cn.stu.pixel_battle.dto.PixelChangeRequest;
import ua.cn.stu.pixel_battle.dto.PixelResponse;
import ua.cn.stu.pixel_battle.service.PixelService;

@Controller
public class PixelWebSocketController {

    private final PixelService pixelService;

    @Autowired
    public PixelWebSocketController(PixelService pixelService) {
        this.pixelService = pixelService;
    }

    @MessageMapping("/change-pixel")
    @SendTo("/topic/pixels")
    public PixelResponse changePixel(PixelChangeRequest message) {
        pixelService.changePixel(message.getX(), message.getY(), message.getColor(), message.getUserId());
        System.out.println("Pixel updated via REST: x=" + message.getX()
                + ", y=" + message.getY()
                + ", color=" + message.getColor()
                + ", user=" + message.getUsername());
        return new PixelResponse(
                message.getX(),
                message.getY(),
                message.getColor(),
                message.getUsername()
        );
    }
}
