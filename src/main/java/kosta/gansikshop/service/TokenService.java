package kosta.gansikshop.service;

import kosta.gansikshop.config.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    public String generateAccessToken(String email) {
        return jwtUtil.generateToken(email);
    }

    public String generateRefreshToken(String email) {
        return jwtUtil.generateRefreshToken(email);
    }

    public boolean validateAccessToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public boolean validateRefreshToken(String token) {
        return jwtUtil.validateRefreshToken(token);
    }

    public void invalidateToken(String token) {
        jwtUtil.invalidateToken(token);
    }

    public String regenerateRefreshToken(String email) {
        return jwtUtil.regenerateRefreshToken(email);
    }

    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }

    public String refreshAccessToken(String refreshToken) {
        log.debug("Refreshing access token using refresh token: {}", refreshToken);
        return jwtUtil.refreshAccessToken(refreshToken);
    }
}
