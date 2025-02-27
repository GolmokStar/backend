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

    // 다이어리 작성 (생성)
    @PostMapping
    public ResponseEntity<?> createDiary(@RequestBody CreateDiaryRequestDto dto) {
        try {
            CreateDiaryResponseDto response = diaryService.createDiary(dto);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "다이어리 생성 내부 로직 오류"));
        }
    }

    // 다이어리 조회 (딘일)
    @GetMapping("{diaryId}")
    public ResponseEntity<?> getDiaryDetail(@PathVariable Long diaryId) {
        try {
            GetDiaryDetailResponseDto response = diaryService.getDiaryDetail(diaryId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "단일 다이어리 조회 내부 로직 오류"));
        }
    }

    // 특정 여행에 포함된 다이어리 모두 조회
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<?> getDiariesByTripId(@PathVariable Long tripId) {
        try {
            List<DiaryListResponseDto> response = diaryService.getDiariesByTripId(tripId);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "여행별 다이어리 목록 조회 내부 로직 오류"));
        }
    }

    // 특정 월 방문 기록 & 일기 조회
    @GetMapping("/{year}/{month}/{userId}")
    public ResponseEntity<?> getMonthlyHistoriesAndDiaries(@PathVariable int year, @PathVariable int month, @PathVariable Long userId) {
        try {
            List<Map<String, Object>> result = diaryService.getMonthlyDiaryHistoriesByMonth(year, month, userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "특정 월 방문 기록 & 일기 조회 내부 로직 오류"));
        }
    }

    // 날자 최신순 다이어리 조회 최대 5개
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentDiaries() {
        try {
            List<GetRecentDiariesResponseDto> result = diaryService.getRecentDiaries();
            return ResponseEntity.ok(result);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "최근 다이어리 조회 내부 로직 오류"));
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
