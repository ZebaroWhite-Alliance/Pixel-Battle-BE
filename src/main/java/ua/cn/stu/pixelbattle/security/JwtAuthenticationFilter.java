package ua.cn.stu.pixelbattle.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.cn.stu.pixelbattle.service.JwtTokenService;


/**
 * A custom authentication filter that validates JWT tokens for incoming requests.
 *
 * <p>This filter extends {@link OncePerRequestFilter}, ensuring that the validation
 * logic runs only once per request. If a valid JWT is found in the
 * {@code Authorization} header, the corresponding {@link CustomUserDetails}
 * is loaded and placed into the {@link SecurityContextHolder}.
 **/
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenService jwtTokenService;

  /**
   * Filters each request to validate and authenticate the user based on JWT.
   *
   * @param req   the {@link HttpServletRequest} object
   * @param res   the {@link HttpServletResponse} object
   * @param chain the {@link FilterChain} to pass the request further
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest req,
                                  HttpServletResponse res,
                                  FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        if (jwtTokenService.validateToken(token)) {
          Long userId = jwtTokenService.getUserId(token);
          CustomUserDetails userDetails = jwtTokenService.loadUserById(userId);
          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities());

          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception ex) {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"Unauthorized\","
            + "\"message\":\"Access token expired or invalid\"}");
        return;
      }
    }

    chain.doFilter(req, res);
  }

}
