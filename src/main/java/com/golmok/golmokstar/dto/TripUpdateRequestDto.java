package com.golmok.golmokstar.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TripUpdateRequestDto {

    @NotNull(message = "userId 입력은 필수입니다.")
    private Long userId;

    @NotBlank(message = "title은 공백이 아니어야 합니다.")
    @Size(max = 8, message = "title은 8글자 이하여야 합니다.")
    private String title;

    @NotNull(message = "startDate 입력은 필수입니다.")
    @FutureOrPresent(message = "startDate는 현재 또는 미래 날짜")
    private LocalDate startDate;

    @NotNull(message = "endDate 입력은 필수입니다.")
    @FutureOrPresent(message = "endDate는 현재 또는 미래 날짜")
    private LocalDate endDate;

    // 종료 날짜가 시작 날짜 이후로 설정
    public boolean isEndDateAfterStartDate() {
        return endDate != null && startDate != null && endDate.isAfter(startDate);
    }
}
