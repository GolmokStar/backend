package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.PlaceDetailResponseDto;
import com.golmok.golmokstar.dto.PlaceListResponseDto;
import com.golmok.golmokstar.dto.PlaceRequestDto;
import com.golmok.golmokstar.dto.PlaceResponseDto;
import com.golmok.golmokstar.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    // 새로운 장소 정보 등록
    @PostMapping("/places")
    public ResponseEntity<?> addPlace(@RequestBody @Valid PlaceRequestDto placeRequestDto) {
        PlaceResponseDto responseDto = placeService.addPlace(placeRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 특정 장소의 상세 정보를 조회
    @GetMapping("/places/{placeId}")
    public ResponseEntity<?> getPlace(@PathVariable Long placeId) {
        PlaceDetailResponseDto responseDto = placeService.getPlace(placeId);
        return ResponseEntity.ok(responseDto);
    }

    // 등록된 모든 장소의 목록을 조회
    // 등록된 배열이 없다면 [] 반환 (에러 X)
    @GetMapping("/places")
    public ResponseEntity<?> getAllPlaces() {
        PlaceListResponseDto responseDto = placeService.getAllPlaces();
        return ResponseEntity.ok(responseDto);
    }

    // 특정 장소 삭제
    @DeleteMapping("/places/{placeId}")
    public ResponseEntity<?> deletePlace(@PathVariable Long placeId) {
        PlaceResponseDto responseDto = placeService.deletePlace(placeId);
        return ResponseEntity.ok(responseDto);
    }
}