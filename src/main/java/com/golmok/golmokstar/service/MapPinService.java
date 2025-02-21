package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.MapPinRequestDto;
import com.golmok.golmokstar.dto.MapPinResponseDto;
import com.golmok.golmokstar.dto.MapPinVisitRequestDto;
import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.entity.Place;
import com.golmok.golmokstar.entity.Trip;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.enums.PinType;
import com.golmok.golmokstar.exception.CustomException;
import com.golmok.golmokstar.repository.MapPinRepository;
import com.golmok.golmokstar.repository.PlaceRepository;
import com.golmok.golmokstar.repository.TripRepository;
import com.golmok.golmokstar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MapPinService {

    private final MapPinRepository mapPinRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    // 장소 찜하기
    @Transactional
    public MapPinResponseDto addMapPin(MapPinRequestDto mapPinRequestDto) {
        // 여행 존재 여부 확인
        Trip trip = tripRepository.findById(mapPinRequestDto.getTripId())
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다"));

        // 사용자 존재 여부 확인
        User user = userRepository.findById(mapPinRequestDto.getUserId())
                .orElseThrow(() -> new CustomException(404, "해당 userId를 찾을 수 없습니다."));

        // 장소 존재 여부 확인
        Place place = placeRepository.findById(mapPinRequestDto.getPlaceId())
                .orElseThrow(() -> new CustomException(404, "해당 placeId를 찾을 수 없습니다."));

        MapPin mapPin = MapPin.builder()
                .trip(trip)
                .user(user)
                .place(place)
                .pinType(mapPinRequestDto.getPinType())
                .createdAt(mapPinRequestDto.getCreatedAt())
                .build();

        mapPinRepository.save(mapPin);

        return MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .message("장소 찜 완료")
                .build();
    }

    // 장소 방문 처리 (값: FAVORED, VISITED_PENDING, RECORDED)
    @Transactional
    public MapPinResponseDto visitPlace(MapPinVisitRequestDto request) {
        // MapPin 존재 여부 확인
        MapPin mapPin = mapPinRepository.findById(request.getPinId())
                .orElseThrow(()-> new CustomException(404, "해당 pinId를 찾을 수 없습니다."));

        // pinType 값 검증 (FAVORED, RECORDED, VISITING_PENDING이 아니면 400 에러)
        if(request.getPinType() != PinType.VISTING_PENDING ) {
            throw new CustomException(400, "올바른 pinType이 아닙니다.");
        }

        // pinType 변경 (FAVORED, RECORDED, VISITING_PENDING 중)
        mapPin.setPinType(request.getPinType());
        mapPinRepository.save(mapPin);

        return MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .message("장소 방문하기 성공")
                .build();

    }
}
