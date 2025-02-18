package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.service.RecordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    /**
     * 방문 기록 작성 (노란색 핀 → 파란색 핀)
     */
    @PostMapping
    public ResponseEntity<?> createRecord(HttpServletRequest request, @RequestBody Map<String, Object> recordData) {
        Long userId = (Long) request.getAttribute("userId"); // JWT로부터 사용자 ID 추출
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        try {
            Long pinId = ((Number) recordData.get("pinId")).longValue();
            Integer rating = (Integer) recordData.get("rating");
            String content = (String) recordData.get("content");
            String photo = (String) recordData.get("photo");

            Map<String, Object> response = recordService.createRecord(userId, pinId, rating, content, photo);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
