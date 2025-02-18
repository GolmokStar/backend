package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.TripCreateRequestDto;
import com.golmok.golmokstar.dto.TripDetailResponseDto;
import com.golmok.golmokstar.dto.TripResponseDto;
import com.golmok.golmokstar.dto.TripUpdateRequestDto;
import com.golmok.golmokstar.entity.Trip;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.exception.CustomException;
import com.golmok.golmokstar.repository.TripRepository;
import com.golmok.golmokstar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    // 여행 일정 등록
    @Transactional
    public TripResponseDto createTrip(TripCreateRequestDto request) {
        // UserId가 존재하는 지 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(404, "해당 userId를 찾을 수 없습니다."));

        Trip trip = Trip.builder()
                .user(user)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        tripRepository.save(trip);

        return TripResponseDto.builder()
                .success(true)
                .tripId(trip.getId())
                .message("여행 일정이 성공적으로 등록되었습니다.")
                .build();

    }

    // 여행 일정 수정
    @Transactional
    public TripResponseDto updateTrip(TripUpdateRequestDto request) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다."));

        trip.setTitle(request.getTitle());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());

        return TripResponseDto.builder()
                .success(true)
                .tripId(trip.getId())
                .message("여행 일정이 성공적으로 수정되었습니다.")
                .build();
    }

    // 특정 여행 일정 조회
    public TripDetailResponseDto getTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다."));

        return TripDetailResponseDto.builder()
                .tripId(trip.getId())
                .userId(trip.getUser().getUserId())
                .title(trip.getTitle())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .build();
    }

    // 여행 일정 삭제
    @Transactional
    public void deleteTrip(Long tripId) {
        if(!tripRepository.existsById(tripId)) {
            throw new CustomException(404, "해당 tripId를 찾을 수 없습니다.");
        }
        tripRepository.deleteById(tripId);
    }
}
