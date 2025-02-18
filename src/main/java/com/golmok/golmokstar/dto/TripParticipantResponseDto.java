package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TripParticipantResponseDto {
    private Long tripParticipantId;
    private String message;
}