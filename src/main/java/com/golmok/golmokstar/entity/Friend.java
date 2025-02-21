package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @JoinColumn(name = "currentUserId", referencedColumnName = "userId")
    private User currentUser;

    @ManyToOne
    @JoinColumn(name = "friendUserId", referencedColumnName = "userId")  // 변경
    private User friendUser;

    @Column(name = "friendshipConfirmedDate")
    private LocalDateTime friendshipConfirmedDate;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer travelCount;


    // friendId만 인자로 받지 않는 생성자
    public Friend(User currentUser, User friendUser, LocalDateTime friendshipConfirmedDate, Integer travelCount) {
        this.currentUser = currentUser;
        this.friendUser = friendUser;
        this.friendshipConfirmedDate = friendshipConfirmedDate;
        this.travelCount = travelCount;
    }
}