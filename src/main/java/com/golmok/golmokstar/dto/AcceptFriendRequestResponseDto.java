package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.RequestStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AcceptFriendRequestResponseDto {
    private final Long requestId;
    private final RequestStatus requestStatus;
    private final LocalDate responseDate;
    private final String message;

    public AcceptFriendRequestResponseDto(Long requestId, RequestStatus requestStatus, LocalDate responseDate) {
        this.requestId = requestId;
        this.requestStatus = requestStatus;
        this.responseDate = responseDate;
        this.message = "친구 요청 수락 완료";
    }
}