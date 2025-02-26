package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trip_participant")
public class TripParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ✅ 자동 증가 설정
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

