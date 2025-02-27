package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    boolean existsByMapPin_PinId(Long pinId);

    List<Record> findByVisitDateBetween(LocalDate start, LocalDate end);

    List<Record> findByVisitDateBetweenAndMapPin_User_UserId(LocalDate start, LocalDate end, Long pinId);
}
