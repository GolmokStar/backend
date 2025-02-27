package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.entity.Place;
import com.golmok.golmokstar.enums.PinType;  // ✅ import 경로 수정
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapPinRepository extends JpaRepository<MapPin, Long> {
    Optional<MapPin> findByPinIdAndPinType(Long pinId, PinType pinType); // ✅ PinType 수정
    Optional<Place> findByGooglePlaceId(String googlePlaceId);      // ✅ googlePlaceId 추가
    List<MapPin> findByTrip_TripIdAndUser_UserId(Long tripId, Long userId);

    //사용자의 모든 여행에 속한 핀 조회 (전체 여행 기록용)
    List<MapPin> findByUser_UserId(Long userId);

    // ✅ 특정 사용자의 모든 MapPin 조회
    List<MapPin> findByUserId(Long userId);

    // ✅ 특정 여행(tripId)과 연결된 장소 조회
    List<MapPin> findByTripId(Long tripId);
}
