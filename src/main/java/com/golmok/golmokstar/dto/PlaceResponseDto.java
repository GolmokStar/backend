package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponseDto {

    private Long placeId;
    private String message;

}
