package com.golmok.golmokstar.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SECRET_KEY = dotenv.get("SECRET_KEY");

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; //24시간 (액세스 토큰)
    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //7일 (리프레시 토큰)
    private final Map<String, String> refreshTokenStore = new HashMap<>(); //리프레시 토큰 저장소

    //Key 객체로 변환
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    static {
        if (SECRET_KEY == null) {
            throw new RuntimeException("SECRET_KEY 환경변수가 설정되지 않았습니다.");
        }
        System.out.println("SECRET_KEY Loaded: " + SECRET_KEY.substring(0, 5) + "****");
    }

    //JWT 액세스 토큰 생성
    public String generateToken(Long userId, String nickname) {
        return Jwts.builder()
                .setSubject(userId.toString()) //userId를 subject로 설정
                .claim("nickname", nickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    //JWT에서 userId 추출 (서명 검증 포함)
    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY) //Key 객체 사용
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject()); //"sub"에서 userId 추출
    }

    //리프레시 토큰 생성 (7일 동안 유효)
    public String generateRefreshToken(Long userId) {
        String refreshToken = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
        refreshTokenStore.put(userId.toString(), refreshToken);
        return refreshToken;
    }

    //JWT 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //리프레시 토큰으로 새로운 액세스 토큰 발급
    public String refreshAccessToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
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
}
