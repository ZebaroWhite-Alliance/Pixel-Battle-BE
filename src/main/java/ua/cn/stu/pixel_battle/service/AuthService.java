package ua.cn.stu.pixel_battle.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixel_battle.dto.AuthRequest;
import ua.cn.stu.pixel_battle.dto.RegisterRequest;
import ua.cn.stu.pixel_battle.model.User;
import ua.cn.stu.pixel_battle.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenService jwtTokenService;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       ua.cn.stu.pixel_battle.service.JWTTokenService jwtTokenService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
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

    public String login(AuthRequest req) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid password");
        }
        return jwtTokenService.createToken(user.getUsername(), user.getId());
    }
}
