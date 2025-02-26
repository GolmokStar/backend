package com.golmok.golmokstar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateRecRequestDto {
    private Long userId;
    private String type;
    private String season;
    private String ageGroup;
    private Integer ranking;
}
