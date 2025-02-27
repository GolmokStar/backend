package com.golmok.golmokstar.service;

import com.golmok.golmokstar.config.JwtUtil;
import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.*;
import com.golmok.golmokstar.entity.Record;
import com.golmok.golmokstar.enums.PinType;
import com.golmok.golmokstar.exception.CustomException;
import com.golmok.golmokstar.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapPinService {

    private final MapPinRepository mapPinRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final JwtUtil jwtUtil;

    // ✅ 장소 찜하기 (FAVORED)
    @Transactional
    public MapPinResponseDto addFavoredMapPin(String token, MapPinRequestDto mapPinRequestDto) {
        Long userId = extractUserIdFromToken(token);
        Trip trip = getTripById(mapPinRequestDto.getTripId());
        User user = getUserById(userId);

        MapPin mapPin = MapPin.builder()
                .trip(trip)
                .user(user)
                .googlePlaceId(mapPinRequestDto.getGooglePlaceId())
                .placeName(mapPinRequestDto.getPlaceName())
                .latitude(mapPinRequestDto.getLatitude())
                .longitude(mapPinRequestDto.getLongitude())
                .pinType(PinType.FAVORED)
                .createdAt(LocalDate.now())
                .build();

        mapPinRepository.save(mapPin);

        int remainingDays = calculateRemainingDays(trip.getStartDate());
        return buildMapPinResponse(mapPin, "장소 찜 완료", remainingDays);
    }

    // ✅ 장소 방문하기 (VISITED_PENDING)
    @Transactional
    public MapPinResponseDto addVisitedMapPin(String token, MapPinVisitRequestDto mapPinVisitRequestDto) {
        Long userId = extractUserIdFromToken(token);
        User user = getUserById(userId);
        Trip trip = getTripById(mapPinVisitRequestDto.getTripId());

        if (!isWithinRange(mapPinVisitRequestDto.getLatitude(), mapPinVisitRequestDto.getLongitude(),
                mapPinVisitRequestDto.getDeviceLatitude(), mapPinVisitRequestDto.getDeviceLongitude())) {
            throw new CustomException(400, "현재 위치가 장소에서 500m 이상 떨어져 있어서 방문을 저장할 수 없습니다.");
        }

        MapPin mapPin = MapPin.builder()
                .trip(trip)
                .user(user)
                .googlePlaceId(mapPinVisitRequestDto.getGooglePlaceId())
                .placeName(mapPinVisitRequestDto.getPlaceName())
                .latitude(mapPinVisitRequestDto.getLatitude())
                .longitude(mapPinVisitRequestDto.getLongitude())
                .pinType(PinType.VISITED_PENDING)
                .createdAt(LocalDate.now())
                .build();

        mapPinRepository.save(mapPin);
        return buildMapPinResponse(mapPin, "장소 방문하기 성공", 0);
    }

    // ✅ 장소 RECORDED 상태로 변경
    @Transactional
    public MapPinResponseDto updateRecordStatus(String token, MapPinRecordRequestDto mapPinRecordRequestDto) {
        Long userId = extractUserIdFromToken(token);
        MapPin mapPin = getMapPinById(mapPinRecordRequestDto.getPinId());

        if (!mapPin.getUser().getUserId().equals(userId)) {
            throw new CustomException(403, "이 장소의 기록을 변경할 권한이 없습니다.");
        }

        mapPin.setPinType(PinType.RECORDED);
        mapPinRepository.save(mapPin);

        return buildMapPinResponse(mapPin, "장소의 상태가 기록 상태로 변경되었습니다.", 0);
    }

    // ✅ 특정 여행(tripId)과 연결된 장소 조회
    @Transactional(readOnly = true)
    public List<MapPinResponseDto> getMapPinsByTripId(String token, Long tripId) {
        extractUserIdFromToken(token);
        List<MapPin> mapPins = mapPinRepository.findByTrip_TripId(tripId);

        return mapPins.stream().map(this::buildDetailedMapPinResponse).collect(Collectors.toList());
    }

    // ✅ 장소 전체 조회
    @Transactional(readOnly = true)
    public List<MapPinResponseDto> getAllMapPins(String token) {
        Long userId = extractUserIdFromToken(token);
        List<MapPin> mapPins = mapPinRepository.findByUser_UserId(userId);

        return mapPins.stream().map(this::buildDetailedMapPinResponse).collect(Collectors.toList());
    }

    // ✅ 500m 거리 검증
    private boolean isWithinRange(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance <= 500;
    }

    // ✅ 상세 MapPin 응답 생성 (buildDetailedMapPinResponse 추가)
    private MapPinResponseDto buildDetailedMapPinResponse(MapPin mapPin) {
        MapPinResponseDto.MapPinResponseDtoBuilder dtoBuilder = MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .googlePlaceId(mapPin.getGooglePlaceId())
                .placeName(mapPin.getPlaceName())
                .latitude(mapPin.getLatitude())
                .longitude(mapPin.getLongitude())
                .pinType(mapPin.getPinType())
                .createdAt(mapPin.getCreatedAt());

        if (mapPin.getPinType() == PinType.RECORDED && mapPin.getTrip() != null) {
            dtoBuilder.tripName(mapPin.getTrip().getTitle())
                    .startDate(mapPin.getTrip().getStartDate())
                    .endDate(mapPin.getTrip().getEndDate());

            Optional<Record> record = recordRepository.findByMapPin(mapPin);
            record.ifPresent(r -> dtoBuilder.rating(r.getRating()));
        }

        return dtoBuilder.build();
    }

    private Long extractUserIdFromToken(String token) {
        return jwtUtil.extractUserId(token.replace("Bearer ", ""));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(401, "해당 userId를 찾을 수 없습니다."));
    }

    private Trip getTripById(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다."));
    }

    private MapPin getMapPinById(Long pinId) {
        return mapPinRepository.findById(pinId)
                .orElseThrow(() -> new CustomException(404, "해당 pinId를 찾을 수 없습니다."));
    }

    private int calculateRemainingDays(LocalDate startDate) {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), startDate);
    }

    private MapPinResponseDto buildMapPinResponse(MapPin mapPin, String message, int remainingDays) {
        return MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .googlePlaceId(mapPin.getGooglePlaceId())
                .placeName(mapPin.getPlaceName())
                .latitude(mapPin.getLatitude())
                .longitude(mapPin.getLongitude())
                .createdAt(mapPin.getCreatedAt())
                .message(message)
                .remainingDays(remainingDays)
                .build();
    }
}
