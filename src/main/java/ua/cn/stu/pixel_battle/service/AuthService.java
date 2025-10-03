package ua.cn.stu.pixel_battle.service;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixel_battle.dto.AuthRequest;
import ua.cn.stu.pixel_battle.dto.AuthResponse;
import ua.cn.stu.pixel_battle.dto.RegisterRequest;
import ua.cn.stu.pixel_battle.model.RefreshToken;
import ua.cn.stu.pixel_battle.model.User;
import ua.cn.stu.pixel_battle.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       JWTTokenService jwtTokenService,
                       RefreshTokenService refreshTokenService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
      this.refreshTokenService = refreshTokenService;
    }

    public void register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        userRepo.save(user);
    }

    @Transactional
    public AuthResponse login(AuthRequest req) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid password");
        }


        refreshTokenService.deleteByUserId(user.getId());


        String accessToken = jwtTokenService.createToken(user.getUsername(), user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public AuthResponse refreshToken(String refreshTokenStr) {

        RefreshToken oldRefreshToken = refreshTokenService.findByToken(refreshTokenStr)
            .map(token -> refreshTokenService.verifyExpiration(token))
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshTokenService.delete(oldRefreshToken);

        String newAccessToken = jwtTokenService.createToken(
            oldRefreshToken.getUser().getUsername(),
            oldRefreshToken.getUser().getId()
        );

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(
            oldRefreshToken.getUser().getId()
        );

        return new AuthResponse(newAccessToken, newRefreshToken.getToken());
    }
}
