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
    private Long pinId;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String googlePlaceId;

    @Column(nullable = false)
    private String placeName;

    @Enumerated(EnumType.STRING)
    private PlaceType placeType;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PinType pinType;

    @Column(nullable = false)
    private LocalDate createdAt;

    // ✅ createdAt이 null이면 자동으로 현재 날짜 설정
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDate.now();
        }
    }
}
