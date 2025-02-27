package com.golmok.golmokstar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TripDropdownResponseDto {
    private List<TripItem> trips;

    @Getter
    @AllArgsConstructor
    public static class TripItem {
        private Long tripId;
        private String title;
    }
}
