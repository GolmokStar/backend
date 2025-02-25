package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.config.JwtUtil;
import com.golmok.golmokstar.dto.TripCreateRequestDto;
import com.golmok.golmokstar.dto.TripDetailResponseDto;
import com.golmok.golmokstar.dto.TripResponseDto;
import com.golmok.golmokstar.dto.TripUpdateRequestDto;
import com.golmok.golmokstar.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final JwtUtil jwtUtil; // ✅ JWT에서 userId를 추출하는 유틸리티 추가

    // 여행 일정 등록 API ( ✅userId → accessToken으로 대체)
    @PostMapping("/trips")
    public ResponseEntity<?> createTrip(
            @RequestHeader("Authorization") String token, // 🔹 클라이언트에서 accessToken을 헤더로 전달
            @RequestBody @Valid TripCreateRequestDto request) {

        // ✅ "Bearer " 접두사 제거 후 JWT에서 userId 추출
        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken); // 🔹 JWT에서 userId 추출

        // EndDate가 StartDate 이후인지 검사
        if(!request.getEndDate().isAfter(request.getStartDate())) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "endDate는 startDate보다 이후여야 합니다.")
            );
        }

        // ✅ userId를 포함하여 Trip 생성 요청
        TripResponseDto response = tripService.createTrip(userId, request);
        return ResponseEntity.ok(response);
    }

    // 여행 일정 수정
    @PutMapping("/trips/{tripId}")
    public ResponseEntity<?> updateTrip(@RequestBody @Valid TripUpdateRequestDto request) {
        if(!request.getEndDate().isAfter(request.getStartDate())) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "endDate는 StartDate보다 이후여야 합니다.")
            );
        }

        TripResponseDto response = tripService.updateTrip(request);
        return ResponseEntity.ok(response);
    }

    // 특정 여행 일정 조회
    @GetMapping("/trips/{tripId}")
    public ResponseEntity<?> getTrip(@PathVariable Long tripId) {
        TripDetailResponseDto response = tripService.getTrip(tripId);
        return ResponseEntity.ok(response);
    }

    // 여행 일정 삭제
    @DeleteMapping("/trips/{tripId}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long tripId) {
        tripService.deleteTrip(tripId);
        return ResponseEntity.ok(Map.of("success", true, "message", "여행 일정이 삭제되었습니다."));
    }
}