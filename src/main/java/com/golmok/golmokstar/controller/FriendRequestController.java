package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.CreateFriendRequestDto;
import com.golmok.golmokstar.dto.CreateFriendRequestResponseDto;
import com.golmok.golmokstar.service.FriendRequestService;
import com.golmok.golmokstar.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;
    private final FriendService friendService;

    public FriendRequestController(FriendRequestService friendRequestService, FriendService friendService) {
        this.friendRequestService = friendRequestService;
        this.friendService = friendService;
    }

    @PostMapping
    public ResponseEntity<?> createFriendRequest(@RequestBody CreateFriendRequestDto dto) {
        try {
            CreateFriendRequestResponseDto response = friendRequestService.createFriendRequest(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}