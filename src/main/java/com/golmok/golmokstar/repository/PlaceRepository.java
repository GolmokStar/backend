package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository <Place, Long> {

    // 중복 장소 확인 (장소명을 통해 조회)
    Optional<Place> findByPlaceName(String placeName);
}