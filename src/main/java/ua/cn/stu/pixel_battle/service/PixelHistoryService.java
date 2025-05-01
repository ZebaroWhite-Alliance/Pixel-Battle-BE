package ua.cn.stu.pixel_battle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixel_battle.model.PixelHistory;
import ua.cn.stu.pixel_battle.repository.PixelHistoryRepository;

import java.util.List;
import java.util.Optional;

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


}

