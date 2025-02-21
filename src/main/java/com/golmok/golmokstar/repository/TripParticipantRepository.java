package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {

    Optional<TripParticipant> findByTrip_IdAndUser_UserId (Long tripId, Long userId);
    List<TripParticipant> findByTripId(Long tripID);
}
