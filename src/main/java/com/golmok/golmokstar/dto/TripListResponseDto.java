package com.golmok.golmokstar.dto;

import jakarta.persistence.GeneratedValue;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TripListResponseDto {
    private List<TripResponseDto> trips;
}
