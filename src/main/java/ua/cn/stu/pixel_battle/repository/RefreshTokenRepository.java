package ua.cn.stu.pixel_battle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.cn.stu.pixel_battle.model.RefreshToken;
import ua.cn.stu.pixel_battle.model.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);
  void deleteByUser(User user);
}
