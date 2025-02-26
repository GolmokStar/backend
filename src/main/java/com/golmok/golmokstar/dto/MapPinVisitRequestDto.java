package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.enums.PinType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapPinVisitRequestDto {

    @NotNull(message = "pinId는 필수입니다.")
    private Long pinId;

    @NotNull(message = "pinType는 필수입니다.")
    private PinType pinType;
}