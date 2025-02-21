package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.dto.AddFriendRequestDto;
import com.golmok.golmokstar.dto.UpdateFriendRequestDto;
import com.golmok.golmokstar.dto.AddFriendResponseDto;
import com.golmok.golmokstar.dto.DeleteFriendResponseDto;
import com.golmok.golmokstar.dto.GetFriendDetailResponseDto;
import com.golmok.golmokstar.dto.UpdateFriendResponseDto;
import com.golmok.golmokstar.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    // 전역 예외 처리 로직 도입해야 함
    @PostMapping
    public ResponseEntity<AddFriendResponseDto> addFriend(@RequestBody AddFriendRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(friendService.addFriend(dto));
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<GetFriendDetailResponseDto> getFriendDetail(@PathVariable Long friendId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(friendService.getFriendDetail(friendId));
    }

    @PutMapping("/{friendId}")
    public ResponseEntity<UpdateFriendResponseDto> updateFriend(@PathVariable Long friendId, UpdateFriendRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(friendService.updateFriend(friendId, dto));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<DeleteFriendResponseDto> deleteFriend(@PathVariable Long friendId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(friendService.deleteFriend(friendId));
    }
}