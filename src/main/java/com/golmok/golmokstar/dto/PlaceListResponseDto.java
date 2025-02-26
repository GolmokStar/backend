package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceListResponseDto {
    private List<PlaceDetailResponseDto> places;
}