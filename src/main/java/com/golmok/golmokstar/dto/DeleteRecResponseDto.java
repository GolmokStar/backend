package com.golmok.golmokstar.dto;

public class DeleteRecResponseDto {
    private final Long recommendationId;
    private final String message;

    public DeleteRecResponseDto(Long recommendationId) {
        this.recommendationId = recommendationId;
        this.message = "추천 항목이 성공적으로 삭제되었습니다.";
    }
}
