package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.entity.Record;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RecordResponseDTO {
    private Long recordId;
    private Long pinId;
    private String placeName;
    private String location;
    private Integer rating;
    private String comment;
    private String photo;
    private LocalDate visitDate;
    private boolean recorded;

    /**
     * 기록이 있는 경우 (Record 존재)
     */
    public RecordResponseDTO(Record record) {
        this.recordId = record.getRecordId();
        this.pinId = record.getMapPin().getPinId();
        this.placeName = record.getMapPin().getPlace().getPlaceName();
        this.location = ""; //위치 정보 필요 시 추가
        this.rating = record.getRating();
        this.comment = record.getComment();
        this.photo = record.getPhoto();
        this.visitDate = record.getVisitDate();
        this.recorded = true; //기록이 존재함
    }

    /**
     * 기록이 없는 경우 (MapPin만 존재)
     */
    public RecordResponseDTO(MapPin pin) {
        this.recordId = null;  //기록이 없으므로 null
        this.pinId = pin.getPinId();
        this.placeName = pin.getPlace().getPlaceName();
        this.location = ""; //위치 정보 필요 시 추가
        this.rating = null; //별점 없음
        this.comment = null; //코멘트 없음
        this.photo = null; //사진 없음
        this.visitDate = pin.getCreatedAt().toLocalDate();
        this.recorded = false; //기록이 존재하지 않음
    }
}
