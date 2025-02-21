package com.golmok.golmokstar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class GetDiaryDetailResponseDto {
    private Long diaryId;
    private Long tripId;
    private LocalDate diaryDate;
    private String content;
    private String photo;
    private String aiDraft;
}
