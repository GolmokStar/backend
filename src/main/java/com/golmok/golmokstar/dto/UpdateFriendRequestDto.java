package com.golmok.golmokstar.dto;

import lombok.Getter;

@Getter
//UpdateTravelCountRequestDto 가 더 나을 것 같기도 합니다.
public class UpdateFriendRequestDto {
    private final Integer travelCount;

    public UpdateFriendRequestDto(Integer travelCount) {
        this.travelCount = travelCount;
    }
}