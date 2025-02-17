package com.golmok.golmokstar.dto.response.trip;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TripDeleteResponseDto {
    private boolean success;
    private String message;
}
