package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.Diary;
import com.golmok.golmokstar.entity.Record;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GetMonthlyDiaryHistoriesDto {
    private String state;
    private LocalDate date;
    private Long diaryId;

    public static GetMonthlyDiaryHistoriesDto fromRecord(Record record) {
        return GetMonthlyDiaryHistoriesDto.builder()
                .state("history")
                .date(record.getVisitDate())
                .diaryId(null)
                .build();
    }

    public static GetMonthlyDiaryHistoriesDto fromDiary(Diary diary) {
        return GetMonthlyDiaryHistoriesDto.builder()
                .state("diary")
                .date(diary.getDiaryDate())
                .diaryId(diary.getDiaryId())
                .build();
    }

}