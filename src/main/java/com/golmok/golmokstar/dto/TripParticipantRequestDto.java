package com.golmok.golmokstar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripParticipantRequestDto {

    @NotNull(message = "tripId는 필수 값입니다.")
    private Long tripId;

    @NotNull(message = "friendUserId는 필수 값입니다.")
    private Long friendUserId;
}
