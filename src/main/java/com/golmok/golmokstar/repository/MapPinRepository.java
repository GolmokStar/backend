package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.enums.PinType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapPinRepository extends JpaRepository<MapPin, Long> {
    Optional<MapPin> findByPinIdAndPinType(Long pinId, PinType pinType); //PinType 수정
    List<MapPin> findByTrip_TripIdAndUser_UserId(Long tripId, Long userId);

    //사용자의 전체 방문 핀을 조회하는 메서드 추가
    List<MapPin> findByUser(User user);

    //사용자의 모든 여행에 속한 핀 조회 (전체 여행 기록용)
    List<MapPin> findByUser_UserId(Long userId);
}
