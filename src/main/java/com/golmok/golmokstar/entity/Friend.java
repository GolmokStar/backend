package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Friend")
@Getter
@Setter
@NoArgsConstructor
// User 간의 친구 관계를 나타내는 Friend 테이블
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId;

    @ManyToOne
    @JoinColumn(name = "currentUserId", referencedColumnName = "userId", nullable = true)
    private User currentUser;

    @ManyToOne
    @JoinColumn(name = "friendUserId", referencedColumnName = "userId", nullable = true)
    private User friendUser;

    @Column(name = "friendshipConfirmedDate", columnDefinition = "DATE", nullable = false)
    private LocalDate friendshipConfirmedDate;

    @Column(name = "travelCountTogether", columnDefinition = "INT DEFAULT 0", nullable = false)
    private Integer travelCountTogether;


    // friendId만 인자로 받지 않는 생성자
    public Friend(User currentUser, User friendUser, LocalDate friendshipConfirmedDate, Integer travelCountTogether) {
        this.currentUser = currentUser;
        this.friendUser = friendUser;
        this.friendshipConfirmedDate = friendshipConfirmedDate;
        this.travelCountTogether = travelCountTogether;
    }
}