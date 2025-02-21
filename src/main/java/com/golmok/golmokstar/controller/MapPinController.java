package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.MapPinRequestDto;
import com.golmok.golmokstar.dto.MapPinResponseDto;
import com.golmok.golmokstar.dto.MapPinVisitRequestDto;
import com.golmok.golmokstar.service.MapPinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MapPinController {

    private final MapPinService mapPinService;

    // 장소 찜하기
    @PostMapping("/mapPin")
    public ResponseEntity<?> addMapPin (@RequestBody @Valid MapPinRequestDto mapPinRequestDto) {
        MapPinResponseDto mapPinResponseDto = mapPinService.addMapPin(mapPinRequestDto);
        return ResponseEntity.ok(mapPinResponseDto);
    }

    // 장소 방문하기
    @PutMapping("/mapPin/visit")
    public ResponseEntity<?> visitPlace (@RequestBody @Valid MapPinVisitRequestDto mapPinVisitRequestDto) {
        MapPinResponseDto mapPinResponseDto = mapPinService.visitPlace(mapPinVisitRequestDto);
        return ResponseEntity.ok(mapPinResponseDto);
    }
}
