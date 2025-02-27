package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Diary;
import com.golmok.golmokstar.service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.golmok.golmokstar.config.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {
    private final DiaryService diaryService;
    private final JwtUtil jwtUtil;


    // 다이어리 작성 (생성)
    @PostMapping
    public ResponseEntity<?> createDiary(@RequestBody CreateDiaryRequestDto dto) {
        try {
            CreateDiaryResponseDto response = diaryService.createDiary(dto);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
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

    // 다이어리 삭제
    @DeleteMapping("{diaryId}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long diaryId) {
        try {
            DeleteDiaryResponseDto response = diaryService.deleteDiary(diaryId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    //ai 일기를 호출해 프론트에게 일기 내용 던져주기
    @GetMapping("/ai")
    public ResponseEntity<?> getAiDiary(
            @RequestHeader("Authorization") String token, //JWT 토큰 받기
            @RequestParam String selectedDate) {  //사용자가 선택한 날짜 받기

        try {
            //JWT에서 userId 추출 Bearer 제거하기
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", "").trim());

            //ai 서버에 userId랑 selectedDate 같이 요청
            String aiDraft = diaryService.getAiDiary(LocalDate.parse(selectedDate), userId);

            //프론트에는 ai_draft 내용 값만 반환
            return ResponseEntity.ok(Map.of("ai_draft", aiDraft));

        } catch (IllegalArgumentException e) { //날짜 형식이 잘못된 경우
            return ResponseEntity.badRequest().body(Map.of("error", "날짜 형식이 잘못되었습니다."));
        } catch (ResponseStatusException e) { //ai 서버 요청 실패 시
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) { //기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "AI 일기 가져오기 실패"));
        }
    }
}