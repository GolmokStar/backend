package com.golmok.golmokstar.entity;

import com.golmok.golmokstar.enums.PinType;
import com.golmok.golmokstar.enums.PlaceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapPin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pinId;  // ✅ Integer → Long으로 변경

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String googlePlaceId; // ✅ Place 엔티티 없이 직접 저장

    @Column(nullable = false)
    private String placeName; // ✅ Place 엔티티 없이 직접 저장

    @Enumerated(EnumType.STRING)
    private PlaceType placeType;        // ✅String -> PlaceType

    @Column(nullable = false)
    private double latitude;   // ✅ 위도 추가

    @Column(nullable = false)
    private double longitude; // ✅ 경도 추가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PinType pinType;

    @Column(nullable = false)
    private LocalDate createdAt;

    // ✅ `createdAt` 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }
}