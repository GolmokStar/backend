package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.service.FriendRequestService;
import com.golmok.golmokstar.service.FriendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService, FriendService friendService) {
        this.friendRequestService = friendRequestService;
    }

    // 친구 요청 목록 조회
    @PostMapping("/{friendCode}")
    public ResponseEntity<?> getFriendRequestsByFriendCode (@PathVariable String friendCode) {
        try {
            List<FriendRequestListResponseDto> response = friendRequestService.getFriendRequestsByFriendCode(friendCode);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "친구 요청 목록 조회 로직 오류"));
        }
    }

    // 친구 요청 생성
    @PostMapping
    public ResponseEntity<?> createFriendRequest(@RequestBody CreateFriendRequestDto dto) {
        try {
            CreateFriendRequestResponseDto response = friendRequestService.createFriendRequest(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "친구 요청 생성 로직 오류"));
        }
    }

    // 친구 요청 수락 및 친구 추가
    @PutMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId) {
        try {
            AcceptFriendRequestResponseDto response = friendRequestService.acceptFriendRequest(requestId);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "친구 요청 수락 로직 오류"));
        }
    }

    // 친구 요청 거절
    @PutMapping("/reject/{requestId}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable Long requestId) {
        try {
            RejectFriendRequestResponse response = friendRequestService.rejectFriendRequest(requestId);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "친구 요청 거절 로직 오류"));
        }
    }

    // 친구 요청 삭제
    @DeleteMapping("{requestId}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long requestId) {
        try {
            DeleteFriendRequestResponseDto response = friendRequestService.deleteFriendRequest(requestId);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "친구 요청 삭제 로직 오류"));
        }
    }
}