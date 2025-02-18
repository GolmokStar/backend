package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("SELECT t FROM Trip t JOIN FETCH t.user WHERE t.tripId = :tripId")
    Optional<Trip> findByIdWithUser(@Param("tripId") Long tripId);
}
