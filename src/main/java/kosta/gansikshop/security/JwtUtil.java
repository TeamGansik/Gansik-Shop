package kosta.gansikshop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String encodedSecretKey;

    private final RedisTemplate<String, Object> redisTemplate;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(encodedSecretKey);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Access Token 발급 (기본 만료 시간: 15분)
    public String generateToken(String email) {
        return generateToken(email, 1000 * 60 * 15);
    }

    // Refresh Token 발급 (기본 만료 시간: 7일)
    public String generateRefreshToken(String email) {
        String refreshToken = generateToken(email, 1000 * 60 * 60 * 24 * 7);
        saveRefreshToken(email, refreshToken);
        return refreshToken;
    }

    // JWT Token 생성
    private String generateToken(String email, long expiration) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    // 토큰 검증 (Blacklisted 확인 포함)
    public boolean validateToken(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                log.debug("Token is blacklisted: {}", token);
                return false;
            }
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.debug("Error validating JWT Token: {}", token, e);
            return false;
        }
    }

    // BlackList 추가 (logout 시 호출)
    public void invalidateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody();
            long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();
            redisTemplate.opsForValue().set(token, "blacklisted", expiration, TimeUnit.MILLISECONDS);
            log.debug("Token invalidated and blacklisted: {}", token);
        } catch (Exception e) {
            log.error("Error invalidating token: {}", token, e);
        }
    }

    // 토큰에서 이메일(사용자 정보) 추출
    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", token, e);
            return null;
        }
    }

    // 토큰이 BlackList에 있는지 확인
    private boolean isTokenBlacklisted(String token) {
        boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(token));
        log.debug("Token blacklist check: {}, Result: {}", token, isBlacklisted);
        return isBlacklisted;
    }

    // Refresh Token 저장 (Redis)
    public void saveRefreshToken(String email, String refreshToken) {
        try {
            redisTemplate.opsForValue().set("refreshToken:" + email, refreshToken, 7, TimeUnit.DAYS);
            log.debug("Refresh token saved for email: {}", email);
        } catch (Exception e) {
            log.error("Error saving refresh token for email: {}", email, e);
        }
    }

    // Refresh Token 유효성 확인
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(refreshToken).getBody();
            String email = claims.getSubject();
            String storedRefreshToken = (String) redisTemplate.opsForValue().get("refreshToken:" + email);

            log.debug("Refresh Token from Redis: {}", storedRefreshToken);
            log.debug("Refresh Token from Request: {}", refreshToken);

            boolean isValid = refreshToken.equals(storedRefreshToken);
            log.debug("Refresh Token Validation Result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.debug("Error validating Refresh Token", e);
            return false;
        }
    }


    // Refresh Token 재생성 및 저장 (기존 토큰 무효화)
    public String regenerateRefreshToken(String email) {
        try {
            redisTemplate.delete("refreshToken:" + email); // 기존 Refresh Token 삭제
            log.debug("Old refresh token deleted for email: {}", email);
            String newRefreshToken = generateRefreshToken(email);
            saveRefreshToken(email, newRefreshToken);
            log.debug("New Refresh Token Generated: {}", newRefreshToken);
            return newRefreshToken;
        } catch (Exception e) {
            log.error("Error regenerating refresh token for email: {}", email, e);
            return null;
        }
    }

    // 새로운 Access Token 발급 (Refresh Token 사용)
    public String refreshAccessToken(String refreshToken) {
        if (validateRefreshToken(refreshToken)) {
            String email = extractUsername(refreshToken);
            return generateToken(email); // 새로운 Access Token 발급
        } else {
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }
}
