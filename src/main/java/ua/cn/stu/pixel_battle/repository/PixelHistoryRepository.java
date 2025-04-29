package ua.cn.stu.pixel_battle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.cn.stu.pixel_battle.model.PixelHistory;
import java.util.List;

@Repository
public interface PixelHistoryRepository extends JpaRepository<PixelHistory, Long> {

    List<PixelHistory> findByXAndY(int x, int y);
    List<PixelHistory> findByUserId(Long userId);
}