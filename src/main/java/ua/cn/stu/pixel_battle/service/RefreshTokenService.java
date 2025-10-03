package ua.cn.stu.pixel_battle.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixel_battle.config.JwtProperties;
import ua.cn.stu.pixel_battle.model.RefreshToken;
import ua.cn.stu.pixel_battle.model.User;
import ua.cn.stu.pixel_battle.repository.RefreshTokenRepository;
import ua.cn.stu.pixel_battle.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final long refreshTokenDurationMs;

  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                             UserRepository userRepository,
                             JwtProperties jwtProperties) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
    this.refreshTokenDurationMs = jwtProperties.getRefreshTokenDurationMs();
  }

  public RefreshToken createRefreshToken(Long userId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    return refreshTokenRepository.save(refreshToken);
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().isBefore(Instant.now())) {
      refreshTokenRepository.delete(token);
      throw new RuntimeException("Refresh token expired. Please login again");
    }
    return token;
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Transactional
  public void deleteByUserId(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    refreshTokenRepository.deleteByUser(user);
  }

  public void delete(RefreshToken token) {
    refreshTokenRepository.delete(token);
  }

}
