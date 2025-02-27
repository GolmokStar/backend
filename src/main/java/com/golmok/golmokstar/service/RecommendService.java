package com.golmok.golmokstar.service;

import com.golmok.golmokstar.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    private static final String AI_SERVER_URL = "http://34.47.73.224:5000/recommend";

    public ResponseEntity<List<RecommendationItem>> getRecommendations(String token) {
        // 1️⃣ 토큰에서 user_id 추출
        Long userId = jwtUtil.extractUserId(token);

        // 2️⃣ AI 서버에 GET 요청 보내기
        String requestUrl = UriComponentsBuilder.fromHttpUrl(AI_SERVER_URL)
                .queryParam("user_id", userId)
                .toUriString();

        // ✅ AI 서버 응답을 `List<Map<String, Object>>`로 받음
        ResponseEntity<List> response = restTemplate.getForEntity(requestUrl, List.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("AI 추천 서버 응답 오류");
        }

        // 3️⃣ JSON 구조 확인 후 필드명 수정
        List<Map<String, Object>> aiRecommendations = response.getBody();
        List<RecommendationItem> recommendations = aiRecommendations.stream()
                .map(item -> new RecommendationItem(
                        (String) item.getOrDefault("place_name", (String) item.get("name")), // 🔥 필드명 변경
                        (Double) item.get("latitude"),
                        (Double) item.get("longitude")
                ))
                .toList();

        return ResponseEntity.ok(recommendations);
    }

    // 4️⃣ `RecommendationItem` 클래스 정의
    public record RecommendationItem(String name, Double latitude, Double longitude) {}
}
