package com.golmok.golmokstar.dto;

import lombok.Getter;

@Getter
public class CreateFriendRequestResponseDto {
    private final long requestId;
    private final String message;

    public CreateFriendRequestResponseDto(long requestId) {
        this.requestId = requestId;
        this.message = "친구 요청이 성공적으로 생성되었습니다.";
    }
}