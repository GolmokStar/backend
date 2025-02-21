package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;

@Entity
@Table(name = "Diary")
@Getter
@Setter
@NoArgsConstructor
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @ManyToOne
    @JoinColumn(name = "tripId", referencedColumnName = "tripId", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private LocalDate diaryDate;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(length = 255)
    private String photo;

    @Column
    private String aiDraft;

    public Diary(Trip trip, LocalDate diaryDate, String content, String photo, String aiDraft) {
        this.trip = trip;
        this.diaryDate = diaryDate;
        this.content = content;
        this.photo = photo;
        this.aiDraft = aiDraft;
    }
}
