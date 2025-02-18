package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @OneToOne
    @JoinColumn(name = "pinId", nullable = false, unique = true)
    private MapPin mapPin;

    @Column(nullable = false, length = 255)
    private String photo; //기본 이미지 설정 필요

    @Column(nullable = false)
    private Integer rating; //1~5점 필수

    @Column(length = 200) //200자 제한
    private String comment;

    @Column(nullable = false)
    private LocalDate visitDate; //기록 작성 날짜
}
