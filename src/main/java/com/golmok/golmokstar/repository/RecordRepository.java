package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Record;
import com.golmok.golmokstar.entity.MapPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findByMapPin(MapPin mapPin);
}
