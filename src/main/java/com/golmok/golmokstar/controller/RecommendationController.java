package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    // 추천 항목 추가
    @PostMapping
    public ResponseEntity<?> createRecommendation(@RequestBody CreateRecRequestDto dto) {
        try {
            CreateRecResponseDto response = recommendationService.createRecommendation(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<?> getRecommendationDetail(@PathVariable Long recommendationId) {
        try {
            GetRecDetailResponseDto response = recommendationService.getRecDetailByRecommendationId(recommendationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRecommendsByUserId(@PathVariable Long userId) {
        try {
            List<GetRecDetailListResponseDto> response = recommendationService.getRecsByUserId(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("{recommendationId}")
    public ResponseEntity<?> deleteRecommendation(@PathVariable Long recommendationId) {
        try {
            DeleteRecResponseDto response = recommendationService.deleteRecommendation(recommendationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
