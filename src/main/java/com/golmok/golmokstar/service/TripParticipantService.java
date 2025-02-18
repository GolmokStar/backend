package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.TripParticipantListResponseDto;
import com.golmok.golmokstar.dto.TripParticipantRequestDto;
import com.golmok.golmokstar.dto.TripParticipantResponseDto;
import com.golmok.golmokstar.entity.Trip;
import com.golmok.golmokstar.entity.TripParticipant;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.exception.CustomException;
import com.golmok.golmokstar.repository.TripParticipantRepository;
import com.golmok.golmokstar.repository.TripRepository;
import com.golmok.golmokstar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripParticipantService {

    private final TripParticipantRepository tripParticipantRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    // 여행 참가자 추가
    @Transactional
    public TripParticipantResponseDto addParticipant(TripParticipantRequestDto request) {
        // 여행 및 사용자 존재 여부 확인
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(()-> new CustomException(404, "해당 여행을 찾을 수 없습니다."));

        User user = userRepository.findById(request.getFriendUserId())
                .orElseThrow(()-> new CustomException(404, "해당 사용자를 찾을 수 없습니다."));

        // 중복 참가자 확인 로직
        if (tripParticipantRepository.findByTrip_TripIdAndUser_UserId(trip.getTripId(), user.getUserId()).isPresent()) {
            throw new CustomException(400, "이미 해당 여행에 참가하고 있는 사용자입니다.");
        }


        // 참가자 저장
        TripParticipant participant = TripParticipant.builder()
                .trip(trip)
                .user(user)
                .build();

        tripParticipantRepository.save(participant);

        return TripParticipantResponseDto.builder()
                .tripParticipantId(participant.getId())
                .message("여행 참가자가 성공적으로 추가되었습니.")
                .build();

    }

    // 특정 여행 참가자 목록 조회
    public List<TripParticipantListResponseDto> getParticipants(Long tripId) {
        List<TripParticipant> participants = tripParticipantRepository.findByTrip_TripId(tripId);

        // 참가자가 없는 경우 - 404 Error
        if (participants.isEmpty()) {
            throw new CustomException(404, "해당 tripId에 대한 참가자를 찾을 수 없습니다.");
        }

        return participants.stream()
                .map(participant -> TripParticipantListResponseDto.builder()
                        .tripParticipantId(participant.getId())
                        .friendUserId(participant.getUser().getUserId())
                        .friendNickname(participant.getUser().getNickname()) // User 엔티티에서 닉네임 가져옴
                        .build())
                .collect(Collectors.toList());
    }

    // 특정 여행 참가자 삭제
    @Transactional
    public TripParticipantResponseDto removeParticipant(Long tripParticipantId) {
        // 참가자가 존재하는지 확인
        TripParticipant participant = tripParticipantRepository.findById(tripParticipantId)
                .orElseThrow(() -> new CustomException(404, "해당 tripParticipantId를 찾을 수 없습니다."));

        // 삭제 로직
        tripParticipantRepository.delete(participant);

        return TripParticipantResponseDto.builder()
                .tripParticipantId(tripParticipantId)
                .message("여행 참가자가 성공적으로 삭제되었습니다.")
                .build();
    }
}