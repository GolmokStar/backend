package com.golmok.golmokstar.dto;

import lombok.Getter;

@Getter
public class DeleteDiaryResponseDto {
    private Long diaryId;
    private String message;

    public DeleteDiaryResponseDto(Long diaryId) {
        this.diaryId = diaryId;
        this.message = "다이어리가 성공적으로 삭제되었습니다.";
    }
}