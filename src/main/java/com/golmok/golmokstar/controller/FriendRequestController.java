package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.service.FriendRequestService;
import com.golmok.golmokstar.service.FriendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService, FriendService friendService) {
        this.friendRequestService = friendRequestService;
    }

    @PostMapping
    public ResponseEntity<?> createFriendRequest(@RequestBody CreateFriendRequestDto dto) {
        try {
            CreateFriendRequestResponseDto response = friendRequestService.createFriendRequest(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getFriendRequestsStatusDetail(@PathVariable Long requestId) {
        try {
            GetFriendRequestStatusDetailResponseDto response = friendRequestService.getFriendRequestStatusDetail(requestId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{requestId}/respond")
    public ResponseEntity<?> respondToFriendRequest(@PathVariable Long requestId, @RequestBody RespondToFriendRequestRequestDto dto) {
        try {
            RespondToFriendRequestResponseDto response = friendRequestService.respondToFriendRequest(requestId, dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 일단 모두 BadRequest로 터지게 해놓았음. 수정 필요
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("{requestId}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long requestId) {
        try {
            DeleteFriendRequestResponseDto response = friendRequestService.deleteFriendRequest(requestId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}