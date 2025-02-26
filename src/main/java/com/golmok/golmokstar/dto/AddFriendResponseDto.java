package com.golmok.golmokstar.dto;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Getter
public class AddFriendResponseDto {
    private final Long friendId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDateTime shipConfirmedAt;
    private final String message;

    public AddFriendResponseDto(long friendId, LocalDateTime shipConfirmedAt) {
        this.friendId = friendId;
        this.shipConfirmedAt = shipConfirmedAt;
        this.message = "친구 관계가 성공적으로 추가되었습니다.";
    }

}