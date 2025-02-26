package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Trip")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private User user; // 여행을 만든 사용자

    @Column(nullable = false, length = 8)
    @NotBlank(message = "title은 공백이 아니어야 합니다.")
    @Size(max = 8, message = "title은 8글자까지 입력 가능합니다.")
    private String title;

    @Column(nullable = false)
    private LocalDate startDate; // 시작 날짜

    @Column(nullable = false)
    private LocalDate endDate; // 종료 날짜

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MapPin> mapPins; // 해당 여행에서 찍힌 핀들

    @Builder
    public Trip(User user, String title, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
