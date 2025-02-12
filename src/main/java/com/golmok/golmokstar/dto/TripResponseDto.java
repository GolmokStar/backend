package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
// 여행 일정 조회 (단일)
public class TripResponseDto {

    private Long userId;
    private Long tripId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

}
