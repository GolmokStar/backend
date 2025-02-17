package com.golmok.golmokstar.dto.request.friendRequest;

import lombok.Getter;

@Getter
public class CreateFriendRequestDto {
    private final Long requesterId;
    private final Long receiverId;

    public CreateFriendRequestDto(Long requesterId, Long reveiverId) {
        this.requesterId = requesterId;
        this.receiverId = reveiverId;
    }
}

