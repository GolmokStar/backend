package com.golmok.golmokstar.dto;

import lombok.Getter;

@Getter
public class DeleteFriendRequestResponseDto {
    private final Long requestId;
    private final String message;

    public DeleteFriendRequestResponseDto(Long requestId) {
        this.requestId = requestId;
        this.message = "친구 요청이 성공적으로 삭제되었습니다.";
    }
}
