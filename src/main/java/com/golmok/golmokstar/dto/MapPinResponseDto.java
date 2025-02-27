package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.enums.PinType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MapPinResponseDto {

    private Long pinId;  //Integer → Long 변경
    private String googlePlaceId;
    private String placeName;
    private double latitude;
    private double longitude;
    private Integer remainingDays;
    private LocalDateTime createdAt;
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
