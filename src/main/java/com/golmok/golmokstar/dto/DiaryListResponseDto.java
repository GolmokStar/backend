package com.golmok.golmokstar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter // 엔티티 객체에 Getter 가 없어 직렬화 대상이 없어서 오류가 났었음.
@AllArgsConstructor
public class DiaryListResponseDto {
    private Long diaryId;
    private Long tripId;
    private LocalDate diaryDate;
    private String content;
    private String photo;
    private String aiDraft;
}
