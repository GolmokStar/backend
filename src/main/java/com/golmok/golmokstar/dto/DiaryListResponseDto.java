package com.golmok.golmokstar.dto;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class DiaryListResponseDto {
    private Long diaryId;
    private Long tripId;
    private LocalDate diaryDate;
    private String content;
    private String photo;
    private String aiDraft;
}
