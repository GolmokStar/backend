package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Diary;
import com.golmok.golmokstar.service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    // 다이어리 목록 날자 최신순으로 조회
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentDiaries(@PathVariable Long diaryId) {
        try {
            List<GetRecentDiariesResponseDto> result = diaryService.getRecentDiaries();
            return ResponseEntity.ok(result);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "최근 다이어리 조회 내부 로직 오류"));
        }
    }

    // 특정 월 방문 기록 & 일기 조회
    @GetMapping("/{year}/{month}")
    public ResponseEntity<?> getMonthlyHistoriesAndDiaries(@PathVariable int year, @PathVariable int month) {
        try {
            List<GetMonthlyDiaryHistoriesDto> result = diaryService.getMonthlyDiaryHistoriesByMonth(year, month);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "특정 월 방문 기록 & 일기 조회 내부 로직 오류"));
        }
    }

}
