package com.golmok.golmokstar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceRequestDto {

    @NotBlank(message = "placeName은 비어 있을 수 없습니다.")
    private String placeName;

    @NotNull(message = "latitude는 필수입니다.")
    private Double latitude;

    @NotNull(message = "longtitude는 필수입니다.")
    private Double longitude;

    @NotBlank(message = "type은 비어 있을 수 없습니다.")
    private String type;

}