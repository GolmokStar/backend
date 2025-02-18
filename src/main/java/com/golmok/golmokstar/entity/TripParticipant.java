package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "TripParticipant")
public class TripParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false) // ✅ FK로 Trip 연결
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // ✅ FK로 User 연결
    private User user;
}
