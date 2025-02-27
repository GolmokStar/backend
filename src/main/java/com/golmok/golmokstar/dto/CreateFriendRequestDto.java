package com.golmok.golmokstar.dto;

import lombok.Getter;

@Getter
public class CreateFriendRequestDto {
    private final String requesterFriendCode;
    private final String receiverFriendCode;

    public CreateFriendRequestDto(String requesterFriendCode, String receiverFriendCode) {
        this.requesterFriendCode = requesterFriendCode;
        this.receiverFriendCode = receiverFriendCode;
    }
}