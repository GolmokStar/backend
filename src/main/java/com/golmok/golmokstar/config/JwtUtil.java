package com.golmok.golmokstar.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SECRET_KEY = dotenv.get("SECRET_KEY");
    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //리프레시 토큰 7일（만료 기한）
    private final Map<String, String> refreshTokenStore = new HashMap<>(); //리프레시 토큰 저장소

    static {
        if (SECRET_KEY == null) {
            throw new RuntimeException("SECRET_KEY 환경변수가 인식이 안됐거나 설정이 안됐거나.");
        }
        System.out.println("SECRET_KEY Loaded: " + SECRET_KEY.substring(0, 5) + "****");
    }

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; //24시간

    public String generateToken(Long userId, String nickname) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("nickname", nickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    //리프레시 토큰 생성 (7일 동안 유효)
    public String generateRefreshToken(Long userId) {
        String refreshToken = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
        refreshTokenStore.put(userId.toString(), refreshToken);
        return refreshToken;
    }

    //JWT 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //리프레시 토큰으로 새로운 액세스 토큰 발급 받기
    public String refreshAccessToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String userId = claims.getSubject();
            if (refreshToken.equals(refreshTokenStore.get(userId))) {
                return generateToken(Long.parseLong(userId), "nickname_placeholder");
            }
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
        return null;
    }

    //로그아웃 시 리프레시 토큰 무효화
    public void revokeRefreshToken(Long userId) {
        refreshTokenStore.remove(userId.toString());
    }

    // ✅ JWT에서 userId 추출하는 메서드 추가
    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY) // 🔹 서명 검증
                .parseClaimsJws(token)     // 🔹 JWT 파싱
                .getBody();

        return claims.get("userId", Long.class); // 🔹 JWT의 userId 클레임 추출
    }

}