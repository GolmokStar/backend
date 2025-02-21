package com.golmok.golmokstar.dto;

import com.golmok.golmokstar.enums.PinType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MapPinRequestDto {

    @NotNull(message = "tripId는 필수 값입니다.")
    private Long tripId;

    @NotNull(message = "userId는 필수 값입니다.")
    private Long userId;

    @NotNull(message = "placeId는 필수 값입니다.")
    private Long placeId;

    @NotNull(message = "pinType는 필수 값입니다.")
    private PinType pinType;

    @NotNull(message = "createdAt는 필수 값입니다.")
    private LocalDateTime createdAt;
}
