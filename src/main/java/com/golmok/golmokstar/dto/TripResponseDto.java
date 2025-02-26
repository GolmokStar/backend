package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TripResponseDto {
    private boolean success;
    private Long tripId;
    private String message;
}