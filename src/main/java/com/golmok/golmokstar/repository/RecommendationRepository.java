package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Friend;
import com.golmok.golmokstar.entity.Recommendation;
import com.golmok.golmokstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findByRecommendationId(Long recommendationId);

    List<Recommendation> findByUser(User user);

    Boolean existsByRecommendationId(Long recommendationId);
}
