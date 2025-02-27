package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.config.JwtUtil;
import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trips") //공통 경로 설정
public class TripController {

    private final TripService tripService;
    private final JwtUtil jwtUtil; //JWT에서 userId를 추출하는 유틸리티 추가

    //여행 일정 등록 (accessToken → userId 추출)
    @PostMapping
    public ResponseEntity<?> createTrip(
            @RequestHeader("Authorization") String token, //클라이언트에서 accessToken을 헤더로 전달
            @RequestBody @Valid TripCreateRequestDto request) {

        //"Bearer " 접두사 제거 후 JWT에서 userId 추출
        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken);

        //날짜 검증
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "endDate는 startDate보다 이후여야 합니다.")
            );
        }

        //userId를 포함하여 Trip 생성 요청
        TripResponseDto response = tripService.createTrip(userId, request);
        return ResponseEntity.ok(response);
    }

    //여행 일정 수정 (accessToken 사용)
    @PutMapping("/{tripId}")
    public ResponseEntity<?> updateTrip(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid TripUpdateRequestDto request) {

        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken);

        if (!request.getEndDate().isAfter(request.getStartDate())) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "endDate는 StartDate보다 이후여야 합니다.")
            );
        }

        TripResponseDto response = tripService.updateTrip(userId, request);
        return ResponseEntity.ok(response);
    }

    //특정 여행 일정 조회
    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTrip(@PathVariable Long tripId) {
        TripDetailResponseDto response = tripService.getTrip(tripId);
        return ResponseEntity.ok(response);
    }

    //여행 일정 삭제 (accessToken 사용)
    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> deleteTrip(
            @RequestHeader("Authorization") String token,
            @PathVariable Long tripId) {

        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken);

        tripService.deleteTrip(userId, tripId);
        return ResponseEntity.ok(Map.of("success", true, "message", "여행 일정이 삭제되었습니다."));
    }

    //현재 진행 중인 여행 조회 API
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentTrip(@RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken);

        Optional<Map<String, Object>> currentTrip = tripService.getCurrentTrip(userId);

        return currentTrip.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(Map.of("message", "현재 진행 중인 여행이 없습니다.")));
    }

    //드롭다운 목록 조회 API (전체 여행 포함)
    @GetMapping("/dropdown")
    public ResponseEntity<TripDropdownResponseDto> getTripDropdown(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        List<TripDropdownResponseDto.TripItem> trips = tripService.getUserTripsForDropdown(userId);
        return ResponseEntity.ok(new TripDropdownResponseDto(trips));
    }
}
