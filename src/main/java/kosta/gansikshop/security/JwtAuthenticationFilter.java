package kosta.gansikshop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String path = request.getRequestURI();
        log.debug("Request Path: {}", path);

        // Refresh Token 처리 로직
        if ("/api/members/refresh-token".equals(path)) {
            String refreshToken = request.getHeader("Refresh-Token");
            if (refreshToken == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No Refresh Token");
                return;
            }
            String jwtRefreshToken = refreshToken.replace("Bearer ", "");
            log.debug("Refresh-Token Header: {}", jwtRefreshToken);

            if (jwtUtil.validateRefreshToken(jwtRefreshToken)) {
                log.debug("Refresh token validated successfully");
                chain.doFilter(request, response);
                return;
            } else {
                log.debug("Invalid refresh token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Refresh Token");
                return;
            }
        }

        // Authorization 헤더에서 JWT 토큰 추출
        String header = request.getHeader("Authorization");
        log.debug("Authorization Header: {}", header);

        // SecurityContext에 인증이 이미 존재하는지 확인
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7); // "Bearer " 제거
            log.debug("Extracted Token: {}", token);

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                var userDetails = customUserDetailsService.loadUserByUsername(username);
                log.debug("User Details Loaded: {}", userDetails.getUsername());

                // 인증 토큰 생성 및 SecurityContext 설정
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set in SecurityContext");
            } else {
                log.debug("Invalid JWT Token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        } else {
            log.debug("No Authorization Header or authentication already set");
        }

        chain.doFilter(request, response);
    }
}
