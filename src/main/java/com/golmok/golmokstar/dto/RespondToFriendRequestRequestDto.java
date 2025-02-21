package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.RequestStatus;
import lombok.Getter;

@Getter
public class RespondToFriendRequestRequestDto {
    private RequestStatus status;
}
