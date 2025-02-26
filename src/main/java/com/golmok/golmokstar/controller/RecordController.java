package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.config.JwtUtil;
import com.golmok.golmokstar.dto.CreateRecordRequestDTO;
import com.golmok.golmokstar.dto.RecordResponseDTO;
import com.golmok.golmokstar.service.RecordService;
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
    private final JwtUtil jwtUtil;

    private Long extractUserId(String token) {
        String accessToken = token.replace("Bearer ", "");
        return jwtUtil.extractUserId(accessToken);
    }

    /**
     * 방문 기록 작성 (노란색 핀 → 파란색 핀)
     */
    @PostMapping
    public ResponseEntity<?> createRecord(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateRecordRequestDTO recordData) {

        Long userId = extractUserId(token);

        try {
            Map<String, Object> response = recordService.createRecord(
                    userId,
                    recordData.getPinId(),
                    recordData.getRating(),
                    recordData.getContent(),
                    recordData.getPhoto()
            );
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 특정 방문 기록 조회
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
     * 특정 여행의 방문한 장소 조회
     * tripId = 0이면 전체 여행 조회
     */
    @GetMapping("/history/travel/{tripId}")
    public ResponseEntity<List<RecordResponseDTO>> getTravelHistory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long tripId) {

        Long userId = extractUserId(token);

        List<RecordResponseDTO> history;
        if (tripId == 0) {
            // 전체 여행 조회
            history = recordService.getAllTravelHistory(userId);
        } else {
            // 특정 여행 조회
            history = recordService.getTravelHistory(userId, tripId);
        }

        return ResponseEntity.ok(history);
    }

    /**
     * 사용자의 최신 방문 기록 리스트 (최대 5개)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<RecordResponseDTO>> getRecentRecords(@RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        return ResponseEntity.ok(recordService.getRecentRecords(userId));
    }

    /**
     * 사용자의 전체 방문 기록을 최신순으로 정렬하여 반환 (기록 안 한 항목 상단 배치)
     */
    @GetMapping("/all")
    public ResponseEntity<List<RecordResponseDTO>> getAllRecords(@RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        return ResponseEntity.ok(recordService.getAllRecords(userId));
    }
}
