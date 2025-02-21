package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.RecordResponseDTO;
import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.entity.Record;
import com.golmok.golmokstar.repository.MapPinRepository;
import com.golmok.golmokstar.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public Map<String, Object> createRecord(Long userId, Long pinId, Integer rating, String content, String photo) {
        //방문 안 한 장소는 기록할 수 없음
        MapPin mapPin = mapPinRepository.findByPinIdAndPinType(pinId, MapPin.PinType.VISITED_PENDING)
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
        mapPin.setPinType(MapPin.PinType.RECORDED);
        mapPinRepository.save(mapPin);

        return Map.of("message", "기록이 저장되었습니다.", "recordId", record.getRecordId());
    }

    /**
     *특정 방문 기록 조회
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
}
