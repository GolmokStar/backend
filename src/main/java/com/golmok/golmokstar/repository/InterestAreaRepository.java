package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.InterestArea;
import com.golmok.golmokstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterestAreaRepository extends JpaRepository<InterestArea, Long> {
    List<InterestArea> findByUser(User user); //특정 유저의 관심 분야 조회

    // 친구 목록 조회 시 InterestArea 엔티티의 String interest 필드만 필요하므로 별도 쿼리 작성
    @Query("SELECT ia.interest FROM InterestArea ia WHERE ia.user = :user")
    List<String> findInterestsByUser(User user);
}