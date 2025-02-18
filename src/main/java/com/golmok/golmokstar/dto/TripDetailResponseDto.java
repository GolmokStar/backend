package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TripDetailResponseDto {
    private Long tripId;
    private Long userId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
}