package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {
    Optional<TripParticipant> findByTrip_TripIdAndUser_UserId(Long tripId, Long userId); // ✅ 여행 ID와 사용자 ID로 조회
    List<TripParticipant> findByTrip_TripId(Long tripId); //여행 ID로 모든 참가자 조회
}


