package com.golmok.golmokstar.dto;

import lombok.Getter;

@Getter
public class CreateRecResponseDto {
    private Long recommendationId;
    private String message;

    public CreateRecResponseDto(Long recommendationId) {
        this.recommendationId = recommendationId;
        this.message = "추천 항목이 성공적으로 추가되었습니다";
    }
}
