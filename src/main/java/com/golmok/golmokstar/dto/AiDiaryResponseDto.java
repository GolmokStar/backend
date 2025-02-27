package com.golmok.golmokstar.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Data
public class AiDiaryResponseDto {
    private LocalDate diaryDate;
    private Long userId;
    private List<String> keywords;
    private String aiDraft;
}
