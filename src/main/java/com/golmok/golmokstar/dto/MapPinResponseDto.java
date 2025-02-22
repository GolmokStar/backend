package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapPinResponseDto {

    private Long pinId;  //Integer → Long 변경
    private String message;

}
