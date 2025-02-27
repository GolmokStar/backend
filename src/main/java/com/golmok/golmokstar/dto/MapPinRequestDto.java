package com.golmok.golmokstar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.golmok.golmokstar.enums.PinType;
import com.golmok.golmokstar.enums.PlaceType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MapPinRequestDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pinId;

    @NotNull(message = "tripId는 필수 값입니다.")
    private Long tripId;

    //userId 필드를 제거 (JWT에서 userId를 추출하도록 변경됨)
//    @NotNull(message = "userId 입력은 필수입니다.")
//    private Long userId;

    // placeId -> googlePlaceId 변경
    @NotNull(message = "googlePlaceId는 필수 값입니다.")
    private String googlePlaceId;

    @NotNull(message = "placeName는 필수 값입니다.")
    private String placeName;

    private PlaceType placeType;

    @NotNull(message = "latitude는 필수 값입니다.")
    private Double latitude;

    @NotNull(message = "longitude는 필수 값입니다.")
    private Double longitude;

    // @NotNull(message = "device latitude는 필수입니다.")
    private double deviceLatitude;

    // @NotNull(message = "device longitude는 필수입니다.")
    private double deviceLongitude;

    @NotNull(message = "pinType는 필수 값입니다.")
    private PinType pinType;

    // ✅ createdAt 제거 : 프론트에서 요청 보내는 값이 아님
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    private LocalDate createdAt = LocalDate.now();

}