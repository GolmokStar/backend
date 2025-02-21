package com.golmok.golmokstar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetFriendDetailResponseDto {
    private final Long friendId;
    private final Long currentUserId;
    private final Long friendUserId;
    // 해당 어노테이션으로 내가 의도하는 대로 표현이 안됨. 수정 필요.
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDateTime shipConfirmedAt;
    private final Integer travelCount;
}