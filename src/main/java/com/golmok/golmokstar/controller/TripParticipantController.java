package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.TripParticipantListResponseDto;
import com.golmok.golmokstar.dto.TripParticipantRequestDto;
import com.golmok.golmokstar.dto.TripParticipantResponseDto;
import com.golmok.golmokstar.entity.Trip;
import com.golmok.golmokstar.entity.TripParticipant;
import com.golmok.golmokstar.service.TripParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TripParticipantController {

    private final TripParticipantService tripParticipantService;

    // 여행 참가자 추가
    @PostMapping("/trip-participants")
    public ResponseEntity<?> addParticipant(@RequestBody @Valid TripParticipantRequestDto request) {
        TripParticipantResponseDto response = tripParticipantService.addParticipant(request);
        return ResponseEntity.ok(response);
    }

    // 특정 여행의 참가자 목록 조회
    @GetMapping("/trip-participants/{tripId}")
    public ResponseEntity<List<?>> getParticipant(@PathVariable Long tripId) {
        List<TripParticipantListResponseDto> response = tripParticipantService.getParticipants(tripId);
        return ResponseEntity.ok(response);
    }

    // 특정 여행에서 참가자를 삭제
    @DeleteMapping("/trip-participants/{tripParticipantId}")
    public ResponseEntity<?> removeParticipant(@PathVariable Long tripParticipantId) {
        TripParticipantResponseDto response = tripParticipantService.removeParticipant(tripParticipantId);
        return ResponseEntity.ok(response);
    }

}