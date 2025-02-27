package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t JOIN FETCH t.user WHERE t.tripId = :tripId")
    Optional<Trip> findByIdWithUser(@Param("tripId") Long tripId);

    List<Trip> findByUser_UserId(Long userId);

    //현재 진행 중인 여행 조회 (startDate ≤ today ≤ endDate)
    Optional<Trip> findByUser_UserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long userId, LocalDate startDate, LocalDate endDate);
}
