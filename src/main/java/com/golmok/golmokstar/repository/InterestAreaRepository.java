package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.InterestArea;
import com.golmok.golmokstar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterestAreaRepository extends JpaRepository<InterestArea, Long> {
    List<InterestArea> findByUser(User user); //특정 유저의 관심 분야 조회
}
