package com.golmok.golmokstar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationResponseDto {
    private List<RecommendationItem> recommendations;

    @Data
    @AllArgsConstructor
    public static class RecommendationItem {
        private String name;
        private Double latitude;
        private Double longitude;
    }
}
