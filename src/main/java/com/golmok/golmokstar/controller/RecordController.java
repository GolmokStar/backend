package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.RecordResponseDTO;
import com.golmok.golmokstar.service.RecordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    /**
     *방문 기록 작성 (노란색 핀 → 파란색 핀)
     */
    @PostMapping
    public ResponseEntity<?> createRecord(HttpServletRequest request, @RequestBody Map<String, Object> recordData) {
        Long userId = (Long) request.getAttribute("userId"); //JWT에서 사용자 ID 추출
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

    /**
     *특정 방문 기록 조회
     */
    @GetMapping("/history/{recordId}")
    public ResponseEntity<?> getRecordById(@PathVariable Long recordId) {
        try {
            RecordResponseDTO recordResponse = recordService.getRecordById(recordId);
            return ResponseEntity.ok(recordResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     *특정 여행의 방문한 장소 조회 (기록한 장소, 방문했지만 기록하지 않은 장소)
     */
    @GetMapping("/history/travel/{travelId}")
    public ResponseEntity<List<RecordResponseDTO>> getTravelHistory(HttpServletRequest request, @PathVariable Long travelId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }

        List<RecordResponseDTO> history = recordService.getTravelHistory(userId, travelId);
        return ResponseEntity.ok(history);
    }
}
