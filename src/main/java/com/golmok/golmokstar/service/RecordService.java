package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.RecordResponseDTO;
import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.entity.Record;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.enums.PinType;
import com.golmok.golmokstar.repository.MapPinRepository;
import com.golmok.golmokstar.repository.RecordRepository;
import com.golmok.golmokstar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final MapPinRepository mapPinRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> createRecord(Long userId, Long pinId, Integer rating, String content, String photo) {
        //방문 안 한 장소는 기록할 수 없음
        MapPin mapPin = mapPinRepository.findByPinIdAndPinType(pinId, PinType.VISITED_PENDING)
                .orElseThrow(() -> new IllegalArgumentException("방문을 안 해서 기록할 수 없습니다."));

        //별점 필수 입력 확인
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("별점 1~5점 필수입니다.");
        }

        //댓글 200자 제한 검증
        if (content != null && content.length() > 200) {
            throw new IllegalArgumentException("입력은 200자까지만 가능합니다.");
        }

        //사진 필수 입력 확인
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("사진을 선택하지 않았습니다.");
        }

        //기록 저장
        Record record = new Record();
        record.setMapPin(mapPin);
        record.setRating(rating);
        record.setComment(content);
        record.setPhoto(photo);
        record.setVisitDate(LocalDate.now());

        record = recordRepository.save(record);

        //핀 타입 변경 (RECORDED)
        mapPin.setPinType(PinType.RECORDED);
        mapPinRepository.save(mapPin);

        //유저의 recordCount 증가
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setRecordCount(user.getRecordCount() + 1);
        userRepository.save(user);

        return Map.of("message", "기록이 저장되었습니다.", "recordId", record.getRecordId());
    }


    /**
     * 특정 방문 기록 조회
     */
    public RecordResponseDTO getRecordById(Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록을 찾을 수 없습니다."));
        return new RecordResponseDTO(record);
    }

    /**
     * 특정 여행의 방문한 장소 조회
     */
    public List<RecordResponseDTO> getTravelHistory(Long userId, Long travelId) {
        List<MapPin> mapPins = mapPinRepository.findByTrip_TripIdAndUser_UserId(travelId, userId);

        return mapPins.stream()
                .map(pin -> recordRepository.findByMapPin(pin)
                        .map(RecordResponseDTO::new)
                        .orElseGet(() -> new RecordResponseDTO(pin)))
                .sorted((r1, r2) -> {
                    if (!r1.isRecorded() && r2.isRecorded()) return -1;
                    if (r1.isRecorded() && !r2.isRecorded()) return 1;
                    return r2.getVisitDate().compareTo(r1.getVisitDate());
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 최신 방문 기록 리스트 (최대 5개)
     */
    @Transactional
    public List<RecordResponseDTO> getRecentRecords(Long userId) {
        //방문 + 기록이 있는 것만 가져옴
        return recordRepository.findByMapPin_User_UserIdOrderByVisitDateDesc(userId, PageRequest.of(0, 5))
                .stream()
                .map(RecordResponseDTO::new)
                .collect(Collectors.toList());
    }


    /**
     * 사용자의 전체 방문 기록을 최신순으로 정렬하여 반환 (기록 안 한 항목 상단 배치)
     */
    @Transactional
    public List<RecordResponseDTO> getAllRecords(Long userId) {
        //사용자를 조회해서 `findByUser(User user)`로 핀 리스트 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<MapPin> mapPins = mapPinRepository.findByUser(user);

        return mapPins.stream()
                .map(pin -> recordRepository.findByMapPin(pin)
                        .map(RecordResponseDTO::new)
                        .orElseGet(() -> new RecordResponseDTO(pin)))
                .sorted((r1, r2) -> {
                    if (!r1.isRecorded() && r2.isRecorded()) return -1;
                    if (r1.isRecorded() && !r2.isRecorded()) return 1;
                    return r2.getVisitDate().compareTo(r1.getVisitDate());
                })
                .collect(Collectors.toList());
    }

    /**
     * 전체 여행 기록 조회 (모든 여행의 기록 가져오기)
     */
    @Transactional
    public List<RecordResponseDTO> getAllTravelHistory(Long userId) {
        List<MapPin> mapPins = mapPinRepository.findByUser_UserId(userId); // 모든 여행의 핀 조회

        return mapPins.stream()
                .map(pin -> recordRepository.findByMapPin(pin)
                        .map(RecordResponseDTO::new)
                        .orElseGet(() -> new RecordResponseDTO(pin)))
                .collect(Collectors.toList());
    }
}
