package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Trip;
import com.golmok.golmokstar.entity.TripParticipant;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.exception.CustomException;
import com.golmok.golmokstar.repository.TripParticipantRepository;
import com.golmok.golmokstar.repository.TripRepository;
import com.golmok.golmokstar.repository.UserRepository;
import com.golmok.golmokstar.config.JwtUtil;  // ✅ JWT 유틸 불러오기
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
    private final TripParticipantRepository tripParticipantRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;  // ✅ JWT 유틸 추가

    // 여행 일정 등록 (JWT에서 userId 추출)
    @Transactional
    public TripResponseDto createTrip(String token, TripCreateRequestDto request) {
        Long userId = jwtUtil.extractUserId(token); // ✅ JWT에서 userId 추출
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(404, "해당 userId를 찾을 수 없습니다."));

        Trip trip = Trip.builder()
                .user(user)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        tripRepository.save(trip);

        // ✅ 여행 생성 시 참가자 등록
        List<TripParticipantResponseDto> addParticipants = request.getParticipants().stream()
                .map(participantId -> {
                    User participant = userRepository.findById(participantId)
                            .orElseThrow(() -> new CustomException(404, "친구의 userId를 찾을 수 없습니다."));

                    if (tripParticipantRepository.existsByTripAndUser(trip, participant)){
                        throw new CustomException(400, "이미 해당 여행에 참여하고 있는 사용자입니다");
                    }

                    TripParticipant tripParticipant = TripParticipant.builder()
                            .trip(trip)
                            .user(participant)
                            .build();

                    tripParticipantRepository.save(tripParticipant);

                    return TripParticipantResponseDto.builder()
                            .tripParticipantId(tripParticipant.getId())
                            .userId(participantId)
                            .message("여행 참가자가 성공적으로 추가되었습니다.")
                            .build();
                }).collect(Collectors.toList());

        return TripResponseDto.builder()
                .success(true)
                .tripId(trip.getTripId())
                .addedParticipants(addParticipants)
                .message("여행 일정이 성공적으로 등록되었습니다.")
                .build();
    }

    // 여행 일정 수정 (JWT에서 userId 추출)
    @Transactional
    public TripResponseDto updateTrip(String token, TripUpdateRequestDto request) {
        Long userId = jwtUtil.extractUserId(token);
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

    // 특정 여행 일정 조회
    public TripDetailResponseDto getTrip(Long tripId) {
        Trip trip = tripRepository.findByIdWithUser(tripId)
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다."));
        return TripDetailResponseDto.from(trip);
    }

    // 여행 일정 삭제 (JWT에서 userId 추출)
    @Transactional
    public void deleteTrip(String token, Long tripId) {
        Long userId = jwtUtil.extractUserId(token);
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


    // 드롭다운용 유저 여행 목록 (JWT에서 userId 추출)
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
