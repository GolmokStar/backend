package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "FriendRequest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "requesterId", referencedColumnName = "userId")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "receiverId", referencedColumnName = "userId")  // 변경
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "requestStatus", nullable = false)
    private RequestStatus requestStatus = RequestStatus.PENDING;

    @Column(name = "requestDate", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "responseDate")
    private LocalDateTime responseDate;

    public FriendRequest(User requester, User receiver, LocalDateTime requestDate) {
        this.requester = requester;
        this.receiver = receiver;
        this.requestDate = requestDate;
    }
}