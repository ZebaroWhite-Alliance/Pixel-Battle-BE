package ua.cn.stu.pixelbattle.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Date;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.cn.stu.pixelbattle.config.JwtProperties;
import ua.cn.stu.pixelbattle.model.User;
import ua.cn.stu.pixelbattle.repository.UserRepository;
import ua.cn.stu.pixelbattle.security.CustomUserDetails;



/**
 * Service for handling JWT (JSON Web Token) creation, validation,
 * and extraction of user-related information.
 *
 * <p>Provides methods to:
 * <ul>
 *   <li>Create JWT tokens with user claims</li>
 *   <li>Validate tokens and check expiration</li>
 *   <li>Extract username and user ID from tokens</li>
 *   <li>Load user details by ID for authentication</li>
 * </ul>
 */
@Service
public class JwtTokenService {

  private final UserRepository userRepository;
  private final String secretKey;
  private final Algorithm algorithm;
  private final long expirationMs;

  /**
   * Constructs JwtTokenService with properties and initializes signing algorithm.
   *
   * @param userRepository repository for fetching user data
   * @param jwtProperties  JWT configuration (secret & expiration)
   */
  public JwtTokenService(UserRepository userRepository, JwtProperties jwtProperties) {
    this.userRepository = userRepository;
    this.secretKey = jwtProperties.getSecret();
    this.expirationMs = jwtProperties.getAccessTokenDurationMs();

    if (this.secretKey == null || this.secretKey.isBlank()) {
      throw new IllegalStateException(
          "JWT secret is not configured. Set JWT_SECRET env or jwt.secret property.");
    }
    this.algorithm = Algorithm.HMAC256(this.secretKey);
  }

  /**
   * Creates a signed JWT token for a given user.
   *
   * @param username the username to include as subject
   * @param userId   the user ID to include as claim
   * @return signed JWT string
   */
  public String createToken(String username, Long userId) {
    return JWT.create()
        .withSubject(username)
        .withClaim("userId", userId)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
        .sign(algorithm);
  }

  /**
   * Validates the given JWT token.
   *
   * @param token JWT token string
   * @return true if valid, false otherwise
   */
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

  /**
   * Extracts username (subject) from token.
   *
   * @param token JWT token string
   * @return username stored in token
   */
  public String getUsername(String token) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT decodedJwt = verifier.verify(token);
    return decodedJwt.getSubject();
  }

  /**
   * Extracts user ID from token claims.
   *
   * @param token JWT token string
   * @return user ID as Long
   */
  public Long getUserId(String token) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT decodedJwt = verifier.verify(token);
    return decodedJwt.getClaim("userId").asLong();
  }

  /**
   * Loads {@link CustomUserDetails} by user ID.
   *
   * @param userId the ID of the user
   * @return user details for authentication
   * @throws UsernameNotFoundException if user is not found
   */
  public CustomUserDetails loadUserById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return new CustomUserDetails(user);

  }
}
