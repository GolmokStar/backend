package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.enums.PinType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapPinVisitRequestDto {

    @NotNull(message = "tripId는 필수입니다.")
    private Long tripId;

    @NotNull(message = "googlePlaceId는 필수입니다.")
    private String googlePlaceId;

    @NotNull(message = "placeName는 필수입니다.")  // ✅ 추가
    private String placeName;

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
