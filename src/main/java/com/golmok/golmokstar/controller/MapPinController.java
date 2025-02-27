package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.MapPinRecordRequestDto;
import com.golmok.golmokstar.dto.MapPinRequestDto;
import com.golmok.golmokstar.dto.MapPinResponseDto;
import com.golmok.golmokstar.dto.MapPinVisitRequestDto;
import com.golmok.golmokstar.service.MapPinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MapPinController {

    private final MapPinService mapPinService;

    // 장소 찜하기
    @PostMapping("/mapPin/favored")
    public ResponseEntity<?> addFavoredMapPin (
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid MapPinRequestDto mapPinRequestDto) {

        MapPinResponseDto mapPinResponseDto = mapPinService.addFavoredMapPin(token, mapPinRequestDto);
        return ResponseEntity.ok(mapPinResponseDto);
    }

    // 장소 방문하기
    @PostMapping("/mapPin/visit")
    public ResponseEntity<?> visitPlace (
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid MapPinVisitRequestDto mapPinVisitRequestDto) {

        MapPinResponseDto mapPinResponseDto = mapPinService.addVisitedMapPin(token, mapPinVisitRequestDto);
        return ResponseEntity.ok(mapPinResponseDto);
    }

    // 장소 RECORDED 상태로 변경
    @PutMapping("/mapPin/record")
    public ResponseEntity<?> recordMapPin (
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid MapPinRecordRequestDto mapPinRecordRequestDto) {

        MapPinResponseDto mapPinResponseDto = mapPinService.updateRecordStatus(token, mapPinRecordRequestDto);
        return ResponseEntity.ok(mapPinResponseDto);
    }

    // 핀 찍힌 장소 전체 조회
    @GetMapping("/mapPin")
    public ResponseEntity<List<MapPinResponseDto>> getAllMapPins (
            @RequestHeader("Authorization") String token) {

        List<MapPinResponseDto> response = mapPinService.getAllMapPins(token);
        return ResponseEntity.ok(response);
    }

    // 특정 여행(tripId)과 연결된 장소 조회
    @GetMapping("/mapPin/{tripId}")
    public ResponseEntity<List<MapPinResponseDto>> getMapPinsByTripId(
            @RequestHeader("Authorization") String token,
            @PathVariable Long tripId){

        List<MapPinResponseDto> response = mapPinService.getMapPinsByTripId(token, tripId);
        return ResponseEntity.ok(response);
    }
}