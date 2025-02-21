package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.RequestStatus;
import lombok.Getter;

@Getter
public class RespondToFriendRequestResponseDto {
    private final Long requestId;
    private final RequestStatus status;
    private final String message;

    public RespondToFriendRequestResponseDto(Long requestId, RequestStatus status) {
        this.requestId = requestId;
        this.status = status;
        this.message = "친구 요청이 성공적으로 처리되었습니다.";
    }
}
