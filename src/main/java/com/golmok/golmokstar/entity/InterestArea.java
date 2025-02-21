package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "InterestArea", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "interest"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterestArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //User와 관계 설정 (FK)
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, length = 20) //관심 분야
    private String interest;
}
