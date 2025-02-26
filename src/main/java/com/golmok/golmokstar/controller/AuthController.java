package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.config.GoogleConfig;
import com.golmok.golmokstar.config.JwtUtil;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.repository.InterestAreaRepository;
import com.golmok.golmokstar.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final InterestAreaRepository interestAreaRepository;
    private final JwtUtil jwtUtil;
    private final GoogleConfig googleConfig;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Google OAuth 로그인
     * 기존 회원이면 JWT 발급해주고
     * 신규 회원이면 Google ID 반환함
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> requestBody) {
        String idToken = requestBody.get("idToken");
        String googleId = extractGoogleIdFromToken(idToken);
        if (googleId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "유효하지 않은 Google ID 토큰"));
        }

        Optional<User> existingUser = userRepository.findByGoogleId(googleId);
        if (existingUser.isPresent()) {
            User user = existingUser.get();

            String accessToken = jwtUtil.generateToken(user.getUserId(), user.getNickname());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

            return ResponseEntity.ok(Map.of(
                    "status", "login",
                    "userId", user.getUserId(),
                    "nickname", user.getNickname(),
                    "friendCode", user.getFriendCode(),
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            ));
        }

        //회원가입이 필요한 경우, 201 Created 반환
        return ResponseEntity.status(201).body(Map.of(
                "status", "signup_required",
                "googleId", googleId
        ));
    }




    /**
     * Google OAuth 회원가입
     * JWT 발급 포함
     */
    @PostMapping("/google/signup")
    public ResponseEntity<?> googleSignup(@RequestBody Map<String, Object> requestBody) {
        try {
            String googleId = (String) requestBody.get("googleId");
            String nickname = (String) requestBody.get("nickname");
            String gender = (String) requestBody.get("gender");
            String birthDateStr = (String) requestBody.get("birthDate");
            Object interestAreasObj = requestBody.get("interestAreas");

            LocalDate birthDate = LocalDate.parse(birthDateStr);

            User newUser = new User();
            newUser.setGoogleId(googleId);
            newUser.setNickname(nickname);
            newUser.setGender(gender);
            newUser.setBirthDate(birthDate);
            newUser.setProfilePhoto("");
            newUser.setFriendCode(String.format("%04d", new Random().nextInt(10000))); //네자리 랜덤숫자 발급
            newUser.setRecordCount(0);

            User savedUser = userRepository.save(newUser);

            String accessToken = jwtUtil.generateToken(savedUser.getUserId(), savedUser.getNickname());
            String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUserId());

            return ResponseEntity.ok(Map.of(
                    "userId", savedUser.getUserId(),
                    "nickname", savedUser.getNickname(),
                    "friendCode", savedUser.getFriendCode(),
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "회원가입 처리 중 서버 오류가 발생했습니다."));
        }
    }


    //JWT 재발급 (POST /auth/refresh)
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");
        String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
        if (newAccessToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "없거나 만료된 리프레시 토큰"));
        }
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    //로그아웃 (POST /auth/logout)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        jwtUtil.revokeRefreshToken(userId);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }


    /**
     * Google ID 토큰에서 Google ID 추출하는 메서드
     */
    private String extractGoogleIdFromToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JSON_FACTORY)
                    .setAudience(googleConfig.getClientIds())  //구글 여러 개의 클라이언트 ID 허용
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                return googleIdToken.getPayload().getSubject();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}