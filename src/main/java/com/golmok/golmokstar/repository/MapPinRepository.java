package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.MapPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MapPinRepository extends JpaRepository<MapPin, Long> {
    Optional<MapPin> findByPinIdAndPinType(Long pinId, MapPin.PinType pinType);
}
