package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Diary;
import com.golmok.golmokstar.entity.Trip;
import com.golmok.golmokstar.repository.DiaryRepository;
import com.golmok.golmokstar.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final TripRepository tripRepository;

    // 다이어리 작성(생성)
    @Transactional
    public CreateDiaryResponseDto createDiary(CreateDiaryRequestDto dto) {
        Trip trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 tripId를 찾을 수 없습니다."));

        // 빈 Diary 객체 생성한 다음 필드 세팅
        // 유효하지 않은 입력 데이터입니다 예외처리 해야함
        Diary diary = new Diary();
        diary.setTrip(trip);
        diary.setDiaryDate(dto.getDiaryDate());
        diary.setContent(dto.getContent());
        diary.setPhoto(dto.getPhoto());
        diary.setAiDraft(dto.getAiDraft());

        diaryRepository.save(diary);

        return new CreateDiaryResponseDto(diary.getDiaryId());
    }

    // 다이어리 조회(단일)
    @Transactional //(readOnly = true)
    public GetDiaryDetailResponseDto getDiaryDetail(Long diaryId) {
        Diary diary = diaryRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 diaryId를 찾을 수 없습니다."));

        return new GetDiaryDetailResponseDto(
                diary.getDiaryId(),
                diary.getTrip().getTripId(),
                diary.getDiaryDate(),
                diary.getContent(),
                diary.getPhoto(),
                diary.getAiDraft()
        );
    }

    // 특정 여행의 포함된 다이어리 모두 조회
    //
    @Transactional
    public List<DiaryListResponseDto> getDiariesByTripId(Long tripId) {
        Trip trip = tripRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 tripId를 찾을 수 없습니다"));

        List<Diary> diaries = diaryRepository.findByTrip(trip);

        if (diaries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 tripId에 대한 다이어리를 찾을 수 없습니다.");
        }

        // diary 형태의 리스트 받아온 다음 DiaryListResponseDto 형태의 리스트로 바꿔서 반환
        return diaries.stream()
                .map(diary -> new DiaryListResponseDto(
                        diary.getDiaryId(),
                        trip.getTripId(),
                        diary.getDiaryDate(),
                        diary.getContent(),
                        diary.getPhoto(),
                        diary.getAiDraft()))
                .collect(Collectors.toList());
    }

    // 다이어리 삭제
    @Transactional
    public DeleteDiaryResponseDto deleteDiary(Long diaryId) {
        if(!diaryRepository.existsById(diaryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 requestId를 찾을 수 없습니다.");
        }

        diaryRepository.deleteById(diaryId);

        return new DeleteDiaryResponseDto(diaryId);
    }
}
