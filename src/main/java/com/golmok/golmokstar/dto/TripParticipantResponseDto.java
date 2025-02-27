package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 개별 참가자 정보 응답을 위한 DTO
public class TripParticipantResponseDto {
    private Long tripParticipantId;
    private Long userId;        // ✅ 여행 참가자의 userId 추가
    private String message;
}