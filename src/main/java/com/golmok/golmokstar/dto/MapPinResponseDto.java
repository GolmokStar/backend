package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapPinResponseDto {

    private Integer pinId;
    private String message;

}
