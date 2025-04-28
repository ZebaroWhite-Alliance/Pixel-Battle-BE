package ua.cn.stu.pixel_battle.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.cn.stu.pixel_battle.service.JWTTokenService;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenService jwtTokenService;

    public JWTAuthenticationFilter(JWTTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/UrlShortener/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            if (jwtTokenService.validateToken(token)) {
                String username = jwtTokenService.getUsername(token);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        new ArrayList<>()
                );


                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
