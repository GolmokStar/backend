package com.golmok.golmokstar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRecordRequestDTO {
    private Long pinId;
    private Integer rating;
    private String content;
    private String photo;
}
