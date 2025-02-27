package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Record;
import com.golmok.golmokstar.entity.MapPin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findByMapPin(MapPin mapPin);

    //최근 기록 5개 조회 (userId 기준)
    @Query("SELECT r FROM Record r WHERE r.mapPin.user.userId = :userId ORDER BY r.visitDate DESC")
    List<Record> findByMapPin_User_UserIdOrderByVisitDateDesc(Long userId, Pageable pageable);

    //사용자의 모든 방문 기록 조회 (최신순, 기록 안 한 항목 상단 배치)
    @Query("SELECT r FROM Record r WHERE r.mapPin.user.userId = :userId ORDER BY r.visitDate DESC")
    List<Record> findAllRecords(@Param("userId") Long userId);
}
