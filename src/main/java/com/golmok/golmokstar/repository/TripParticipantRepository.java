package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {

    // ✅ tripId와 userId는 Trip과 User 엔티티 내부에 있으므로 경로를 올바르게 지정
    Optional<TripParticipant> findByTrip_TripIdAndUser_UserId(Long tripId, Long userId);

    // ✅ 변수 이름을 올바르게 변경 (tripID → tripId)
    List<TripParticipant> findByTrip_TripId(Long tripId);

    // ✅ JPQL을 사용하는 방법 (추가 옵션)
    @Query("SELECT tp FROM TripParticipant tp WHERE tp.trip.tripId = :tripId AND tp.user.userId = :userId")
    Optional<TripParticipant> findParticipant(@Param("tripId") Long tripId, @Param("userId") Long userId);
}
