package com.golmok.golmokstar.repository;

import com.golmok.golmokstar.entity.Diary;
import com.golmok.golmokstar.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByDiaryId(Long diaryId);

    List<Diary> findByTrip(Trip trip);

    // diaryDate 기준 최신순으로 최대 5개의 다이어리를 조회하는 쿼리
    List<Diary> findTop5ByOrderByDiaryDateDesc();

    // diaryDate 가 특정 기간 안에 해당되는 Diary 조회
    List<Diary> findByDiaryDateBetween(LocalDate start, LocalDate end);
}
