package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.entity.Record;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class RecordResponseDTO {
    private Long recordId;
    private Long pinId;
    private String placeName;
    private String tripTitle;
    private String googlePlaceId;
    private Integer rating;
    private String comment;
    private String photo;
    private String visitDate;
    private boolean recorded;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ✅ 기록이 있는 경우
    public RecordResponseDTO(Record record) {
        this.recordId = record.getRecordId();
        this.pinId = record.getMapPin().getPinId();
        this.placeName = record.getMapPin().getPlaceName();  // ✅ mapPin에서 직접 가져오기
        this.tripTitle = record.getMapPin().getTrip().getTitle();
        this.googlePlaceId = record.getMapPin().getGooglePlaceId(); // ✅ 추가
        this.rating = record.getRating();
        this.comment = record.getComment();
        this.photo = record.getPhoto();
        this.visitDate = record.getVisitDate().format(DATE_FORMATTER);
        this.recorded = true;
    }

    // ✅ 기록이 없는 경우
    public RecordResponseDTO(MapPin pin) {
        this.recordId = null;
        this.pinId = pin.getPinId();
        this.placeName = pin.getPlaceName();  // ✅ mapPin에서 직접 가져오기
        this.tripTitle = pin.getTrip().getTitle();
        this.googlePlaceId = pin.getGooglePlaceId(); // ✅ 추가
        this.rating = null;
        this.comment = null;
        this.photo = null;
        this.visitDate = null;
        this.recorded = false;
    }
}
