package com.golmok.golmokstar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.golmok.golmokstar.enums.PinType;
import com.golmok.golmokstar.enums.PlaceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MapPinResponseDto {

    private Long pinId;  //Integer → Long 변경
    private String googlePlaceId;
    // ✅ placeType 추가
    private PlaceType placeType;
    private String placeName;
    private double latitude;
    private double longitude;
    private Integer remainingDays;

    // ✅ LocalDateTime -> LocalDate 변경
    // ✅ JSON 변환 시 "yyyy-MM-dd" 형식 유지
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    private String message;

    // 조회하기 ) tripId별 조회
    private PinType pinType;
    private Long tripId;
    private String title;

    // 조회하기 ) RECORDED일 경우 추가 정보
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer rating;
}