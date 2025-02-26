package com.golmok.golmokstar.dto;

import lombok.Getter;

@Getter
public class AddFriendRequestDto {
    private Long currentUserId;
    private Long friendUserId;
}