package ua.cn.stu.pixel_battle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixel_battle.dto.PixelHistoryDTO;
import ua.cn.stu.pixel_battle.model.PixelHistory;
import ua.cn.stu.pixel_battle.repository.PixelHistoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PixelHistoryService {

    private final PixelHistoryRepository repository;

    @Autowired
    public PixelHistoryService(PixelHistoryRepository repository) {
        this.repository = repository;
    }

    public List<PixelHistory> getAllAfterId(Long id) {
        return repository.findByIdGreaterThanOrderByIdAsc(id);
    }

    public Optional<PixelHistory> getNextAfterId(Long id) {
        return repository.findByIdGreaterThanOrderByIdAsc(id).stream().findFirst();
    }


    public List<PixelHistoryDTO> getHistoryByUserId(Long userId) {
        List<PixelHistory> history = repository.findByUserId(userId);
        return history.stream()
                .map(h -> new PixelHistoryDTO(
                        h.getId(),
                        h.getX(),
                        h.getY(),
                        h.getNewColor()

                ))
                .collect(Collectors.toList());
    }
}

