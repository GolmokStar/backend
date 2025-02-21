package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceDetailResponseDto {
    private Long placeId;
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String type;
}
