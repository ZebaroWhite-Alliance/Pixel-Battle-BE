package ua.cn.stu.pixel_battle.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixel_battle.config.JwtProperties;
import ua.cn.stu.pixel_battle.model.User;
import ua.cn.stu.pixel_battle.repository.UserRepository;
import ua.cn.stu.pixel_battle.security.CustomUserDetails;

import java.util.Date;

@Service
public class JWTTokenService {

    private final UserRepository userRepository;
    private final String secretKey;
    private final Algorithm algorithm;
    private final long expirationMs;


    public JWTTokenService(UserRepository userRepository, JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.secretKey = jwtProperties.getSecret();
        this.expirationMs = jwtProperties.getAccessTokenDurationMs();

        if (this.secretKey == null || this.secretKey.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set JWT_SECRET env or jwt.secret property.");
        }
        this.algorithm = Algorithm.HMAC256(this.secretKey);
    }


    public String createToken(String username, Long userId) {
        return JWT.create()
                .withSubject(username)
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();  
    }

    public Long getUserId(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("userId").asLong();
    }

    public CustomUserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);

    }
}
