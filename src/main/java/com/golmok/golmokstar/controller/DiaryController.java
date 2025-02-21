package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Diary;
import com.golmok.golmokstar.service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/diary")
public class DiaryController {
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping
    public ResponseEntity<?> createDiary(@RequestBody CreateDiaryRequestDto dto) {
        try {
            CreateDiaryResponseDto response = diaryService.createDiary(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("{diaryId}")
    public ResponseEntity<?> getDiaryDetail(@PathVariable Long diaryId) {
        try {
            GetDiaryDetailResponseDto response = diaryService.getDiaryDetail(diaryId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<?> getDiariesByTripId(@PathVariable Long tripId) {
        try {
            List<DiaryListResponseDto> response = diaryService.getDiariesByTripId(tripId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("{diaryId}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long diaryId) {
        try {
            DeleteDiaryResponseDto response = diaryService.deleteDiary(diaryId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
