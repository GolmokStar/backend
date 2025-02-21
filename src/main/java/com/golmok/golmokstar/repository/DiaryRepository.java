package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Diary;
import com.golmok.golmokstar.entity.FriendRequest;
import com.golmok.golmokstar.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByDiaryId(Long diaryId);

    List<Diary> findByTrip(Trip trip);
}
