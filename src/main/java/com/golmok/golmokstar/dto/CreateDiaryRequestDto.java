package com.golmok.golmokstar.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateDiaryRequestDto {
    private Long tripId;
    private LocalDate diaryDate;
    private String content;
    private String photo;
    private String aiDraft;
}