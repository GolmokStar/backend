package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.enums.PinType;  // ✅ import 경로 수정
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapPinRepository extends JpaRepository<MapPin, Long> {
    Optional<MapPin> findByPinIdAndPinType(Long pinId, PinType pinType); // ✅ PinType 수정
    List<MapPin> findByTrip_TripIdAndUser_UserId(Long tripId, Long userId);
}
