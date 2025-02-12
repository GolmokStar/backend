package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
