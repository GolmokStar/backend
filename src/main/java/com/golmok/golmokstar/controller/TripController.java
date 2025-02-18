package com.golmok.golmokstar.controller;

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

    // 여행 일정 등록 API
    @PostMapping("/trips")
    public ResponseEntity<?> createTrip(@RequestBody @Valid TripCreateRequestDto request) {
        // EndDate가 StartDate 이후인지 검사
        if(!request.getEndDate().isAfter(request.getStartDate())) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "endDate는 startDate보다 이후여야 합니다.")
            );
        }

        TripResponseDto response = tripService.createTrip(request);
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
