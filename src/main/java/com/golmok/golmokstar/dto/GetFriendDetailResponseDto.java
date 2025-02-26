package com.golmok.golmokstar.dto;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
public class GetFriendDetailResponseDto {
    private final Long friendId;
    private final Long currentUserId;
    private final Long friendUserId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDateTime shipConfirmedAt;
    private final Integer travelCount;

    public GetFriendDetailResponseDto(Long friendId, Long currentUserId, Long friendUserId, LocalDateTime shipConfirmedAt, Integer travelCount) {
        this.friendId = friendId;
        this.currentUserId = currentUserId;
        this.friendUserId = friendUserId;
        this.shipConfirmedAt = shipConfirmedAt;
        this.travelCount = travelCount;
    }
}