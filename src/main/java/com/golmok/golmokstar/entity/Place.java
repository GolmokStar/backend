package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Column(nullable = false)
    private String placeName; // 장소명

    @Column(nullable = false)
    private Double latitude; // 위도

    @Column(nullable = false)
    private Double longitude; // 경도

    @Column(nullable = false, length = 20)
    private String type; // 구글맵 API types 값
}
