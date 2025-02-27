package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.Trip;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class TripDetailResponseDto {
    private Long tripId;
    private Long userId;
    private String title;
    private String startDate;  //"yyyyMMdd" 형식의 문자열
    private String endDate;    //"yyyyMMdd" 형식의 문자열

    public static TripDetailResponseDto from(Trip trip) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return TripDetailResponseDto.builder()
                .tripId(trip.getTripId())
                .userId(trip.getUser().getUserId())
                .title(trip.getTitle())
                .startDate(trip.getStartDate().format(formatter))  //날짜 변환
                .endDate(trip.getEndDate().format(formatter))      //날짜 변환
                .build();
    }
}
