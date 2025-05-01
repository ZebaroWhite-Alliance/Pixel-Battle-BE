package ua.cn.stu.pixel_battle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.cn.stu.pixel_battle.dto.PixelResponse;
import ua.cn.stu.pixel_battle.model.PixelHistory;
import ua.cn.stu.pixel_battle.service.PixelHistoryService;

import java.util.List;

@RestController
@RequestMapping("/pixel-battle/api/v1/history")
public class PixelHistoryController {

    private final PixelHistoryService service;

    @Autowired
    public PixelHistoryController(PixelHistoryService service) {
        this.service = service;
    }

    @GetMapping("/after/{id}")
    public List<PixelResponse> getAllAfter(@PathVariable Long id) {
        return service.getAllAfterId(id).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @GetMapping("/next/{id}")
    public ResponseEntity<PixelResponse> getNext(@PathVariable Long id) {
        return service.getNextAfterId(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private PixelResponse mapToResponse(PixelHistory history) {
        return new PixelResponse(history.getX(), history.getY(), history.getNewColor(), history.getUser().getUsername());
    }
}
