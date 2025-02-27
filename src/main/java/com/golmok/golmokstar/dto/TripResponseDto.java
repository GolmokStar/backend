package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TripResponseDto {
    private boolean success;
    private Long tripId;
    // ✅ 참가자 목록 추가
    private List<TripParticipantResponseDto> addedParticipants;
    private String message;
}