package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MapPin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapPin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pinId;

    @ManyToOne
    @JoinColumn(name = "tripId", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "placeId", nullable = false)
    private Place place;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PinType pinType; // FAVORED, VISITED_PENDING, RECORDED

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum PinType {
        FAVORED, VISITED_PENDING, RECORDED
    }
}
