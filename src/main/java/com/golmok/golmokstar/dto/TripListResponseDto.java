package com.golmok.golmokstar.dto;

import jakarta.persistence.GeneratedValue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TripListResponseDto {
    private List<TripResponseDto> trips;
}
