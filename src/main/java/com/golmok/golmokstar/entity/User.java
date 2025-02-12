package com.golmok.golmokstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String googleId;

    @Column(nullable = false, length = 8) //닉네임 필수 입력
    private String nickname;

    @Column(nullable = false, length = 20) //성별 필수 입력
    private String gender;

    @Column(nullable = false) //생년월일 필수 입력
    private LocalDate birthDate;

    @Column(nullable = false, unique = true, length = 4) //친구 추가용 4자리 코드
    private String friendCode;

    @Column(nullable = false) //프로필 사진
    private String profilePhoto;

    @Column(nullable = false)
    private Integer travelCount = 0; //기본값 0

    //관심 분야 테이블과 연결 (1:N 관계)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestArea> interestAreas;
}
