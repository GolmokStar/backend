package com.golmok.golmokstar.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetRecDetailListResponseDto {
    private Long recommendationId;
    private String type;
    private String season;
    private String ageGroup;
    private Integer ranking;
}
