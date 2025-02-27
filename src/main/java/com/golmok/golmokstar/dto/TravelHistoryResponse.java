package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.entity.Record;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class TravelHistoryResponse {

    private final int totalPlaces;
    private final List<PlaceHistory> history;

    public TravelHistoryResponse(List<PlaceHistory> history) {
        this.totalPlaces = history.size();
        this.history = history;
    }

    @Getter
    public static class PlaceHistory {
        private final Long pinId;
        private final String placeName;
        private final String location;
        private final String status;
        private final LocalDate visitDate;
        private final boolean recorded;

        private Integer rating;
        private String comment;
        private String photo;

        public PlaceHistory(MapPin pin, Record record) {
            this.pinId = pin.getPinId();
            // ✅ Place 엔티티 없이 MapPin에서 직접 가져옴
            // 수정 전 : this.placeName = pin.getPlace().getPlaceName();
            this.placeName = pin.getPlaceName();
            this.location = "위치 정보 없음"; //필요하면 Place 엔티티에 필드 추가

            if (record == null) {
                this.status = "방문했지만 기록 없음";
                this.recorded = false;
            } else {
                this.status = "기록 완료";
                this.recorded = true;
                this.rating = record.getRating();
                this.comment = record.getComment();
                this.photo = record.getPhoto();
            }
            this.visitDate = pin.getCreatedAt().toLocalDate(); //방문 날짜
        }

        /**
         * 방문 기록 없는 장소를 최상단, 이후 기록 완료된 장소를 날짜 최신순 정렬
         */
        public static int compareByStatus(PlaceHistory a, PlaceHistory b) {
            if (a.recorded == b.recorded) {
                return b.visitDate.compareTo(a.visitDate); // 최신 방문 순
            }
            return a.recorded ? 1 : -1;
        }
    }
}
