package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.enums.PinType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapPinVisitRequestDto {

    // ❌ pinId는 반환 데이터로 변경
//    @NotNull(message = "pinId는 필수입니다.")
//    private Long pinId;

    // ✅입력 데이터 수정에 따라 추가

    @NotNull(message = "tripId는 필수입니다.")
    private Long tripId;

    @NotNull(message = "googlePlaceId는 필수입니다.")
    private String googlePlaceId;

    @NotNull(message = "latitude는 필수입니다.")
    private double latitude;

    @NotNull(message = "longitude는 필수입니다.")
    private double longitude;

    @NotNull(message = "device latitude는 필수입니다.")
    private double deviceLatitude;

    @NotNull(message = "device longitude는 필수입니다.")
    private double deviceLongitude;

    @NotNull(message = "pinType는 필수입니다.")
    private PinType pinType;
}