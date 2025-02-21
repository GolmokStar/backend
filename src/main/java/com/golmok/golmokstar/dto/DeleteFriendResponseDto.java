package com.golmok.golmokstar.dto;

import lombok.Getter;

@Getter
public class DeleteFriendResponseDto {
    private final Long friendId;
    private final String message;

    public DeleteFriendResponseDto(Long friendId) {
        this.friendId = friendId;
        this.message = "친구 관계가 성공적으로 삭제되었습니다.";
    }
}