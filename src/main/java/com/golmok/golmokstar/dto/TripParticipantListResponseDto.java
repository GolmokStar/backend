package com.golmok.golmokstar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TripParticipantListResponseDto {

    private Long tripParticipantId;
    private Long friendUserId;
    private String friendNickname;

}