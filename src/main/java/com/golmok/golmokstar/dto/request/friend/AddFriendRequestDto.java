package com.golmok.golmokstar.dto.request.friend;

import lombok.Getter;

@Getter
public class AddFriendRequestDto {
    private Long currentUserId;
    private Long friendUserId;
}
