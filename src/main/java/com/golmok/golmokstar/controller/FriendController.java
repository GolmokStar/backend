package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    // 친구 목록 조회
    @GetMapping("/{friendCode}")
    public ResponseEntity<?> getFriendListByFriendCode(@PathVariable String friendCode) {
        try {
            List<GetFriendListResponseDto> response = friendService.getFriendListByFriendCode(friendCode);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "친구 목록 조회 내부 로직 오류"));
        }
    }

    // 친구 삭제
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long friendId) {
        try {
            DeleteFriendResponseDto response = friendService.deleteFriend(friendId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "친구 삭제 내부 로직 오류"));
        }
    }
}