package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    boolean existsByMapPin_PinId(Long pinId);
}
