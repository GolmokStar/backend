package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetFriendRequestStatusDetailResponseDto {
    private final Long requestId;
    private final Long requesterId;
    private final Long receiverId;
    private final RequestStatus requestStatus;
    // 해당 어노테이션으로 내가 의도하는 대로 표현이 안됨. 수정 필요.
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDateTime requestDate;
    // 해당 어노테이션으로 내가 의도하는 대로 표현이 안됨. 수정 필요.
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDateTime responseDate;
}
