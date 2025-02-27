package com.golmok.golmokstar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestListResponseDto {
    private final Long requestId;
    private final String nickname;
}