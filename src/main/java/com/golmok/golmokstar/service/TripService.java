package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Trip;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.exception.CustomException;
import com.golmok.golmokstar.repository.TripRepository;
import com.golmok.golmokstar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    //여행 일정 등록
    @Transactional
    public TripResponseDto createTrip(Long userId, TripCreateRequestDto request) {
        User user = userRepository.findById(userId)
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
                .tripId(trip.getTripId())
                .message("여행 일정이 성공적으로 등록되었습니다.")
                .build();
    }

    //여행 일정 수정 (userId 추가)
    @Transactional
    public TripResponseDto updateTrip(Long userId, TripUpdateRequestDto request) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다."));

        if (!trip.getUser().getUserId().equals(userId)) {
            throw new CustomException(403, "권한이 없습니다.");
        }

        trip.setTitle(request.getTitle());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());

        return TripResponseDto.builder()
                .success(true)
                .tripId(trip.getTripId())
                .message("여행 일정이 성공적으로 수정되었습니다.")
                .build();
    }

    //특정 여행 일정 조회
    public TripDetailResponseDto getTrip(Long tripId) {
        Trip trip = tripRepository.findByIdWithUser(tripId)
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다."));

        return TripDetailResponseDto.builder()
                .tripId(trip.getTripId())
                .userId(trip.getUser().getUserId())
                .title(trip.getTitle())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .build();
    }

    //여행 일정 삭제 (userId 체크 추가)
    @Transactional
    public void deleteTrip(Long userId, Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다."));

        if (!trip.getUser().getUserId().equals(userId)) {
            throw new CustomException(403, "권한이 없습니다.");
        }

        tripRepository.delete(trip);
    }

    //현재 진행 중인 여행 조회
    public Optional<Map<String, Object>> getCurrentTrip(Long userId) {
        //현재 시간을 KST 기준으로 변환
        LocalDate today = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate();

        Optional<Trip> currentTrip = tripRepository.findByUser_UserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                userId, today, today);

        return currentTrip.map(trip -> Map.of(
                "tripId", trip.getTripId(),
                "title", trip.getTitle()
        ));
    }

    @Transactional(readOnly = true)
    public List<TripDropdownResponseDto.TripItem> getUserTripsForDropdown(Long userId) {
        List<TripDropdownResponseDto.TripItem> trips = tripRepository.findByUser_UserId(userId)
                .stream()
                .map(trip -> new TripDropdownResponseDto.TripItem(trip.getTripId(), trip.getTitle()))
                .collect(Collectors.toList());

        trips.add(new TripDropdownResponseDto.TripItem(0L, "전체 여행"));
        return trips;
    }

}
