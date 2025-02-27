package com.golmok.golmokstar.dto;

import lombok.Getter;

@Getter
public class CreateDiaryResponseDto {
    private final Long diaryId;
    private final String message;

    public CreateDiaryResponseDto(Long diaryId) {
        this.diaryId = diaryId;
        this.message = "다이어리가 성공적으로 작성되었습니다.";
    }
}