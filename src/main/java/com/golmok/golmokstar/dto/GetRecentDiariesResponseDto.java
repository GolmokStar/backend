package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.Diary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GetRecentDiariesResponseDto {
    private Long diaryId;
    private Long tripId;
    private LocalDate diaryDate;
    private String content;
    private String photo;
    private String aiDraft;

    public static GetRecentDiariesResponseDto fromEntityToDto(Diary diary) {
        return GetRecentDiariesResponseDto.builder()
                .diaryId(diary.getDiaryId())
                .tripId(diary.getTrip().getTripId())
                .diaryDate(diary.getDiaryDate())
                .content(diary.getContent())
                .photo(diary.getPhoto())
                .aiDraft(diary.getAiDraft())
                .build();
    }
}