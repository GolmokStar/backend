package com.golmok.golmokstar.service;

import com.golmok.golmokstar.config.JwtUtil;
import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.*;
import com.golmok.golmokstar.entity.Record;
import com.golmok.golmokstar.enums.PinType;
import com.golmok.golmokstar.enums.PlaceType;
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

        // ✅ enum에 없는 값이거나 null -> unknown
        PlaceType placeType = (mapPinRequestDto.getPlaceType() != null) ?
                PlaceType.fromString(mapPinRequestDto.getPlaceType().toString()) : PlaceType.UNKNOWN;


        MapPin mapPin = MapPin.builder()
                .trip(trip)
                .user(user)
                .googlePlaceId(mapPinRequestDto.getGooglePlaceId())
                .placeName(mapPinRequestDto.getPlaceName())
                .placeType(placeType)
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
        return buildVisitResponse(mapPin, "장소 방문하기 성공");
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

    private MapPinResponseDto buildDetailedMapPinResponse(MapPin mapPin) {
        int remainingDays = (mapPin.getTrip() != null) ? calculateRemainingDays(mapPin.getTrip().getStartDate()) : 0;

        MapPinResponseDto.MapPinResponseDtoBuilder dtoBuilder = MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .googlePlaceId(mapPin.getGooglePlaceId())
                .placeName(mapPin.getPlaceName())
                .placeType(mapPin.getPlaceType() != null ? mapPin.getPlaceType() : PlaceType.UNKNOWN) // ✅ placeType 반환 추가
                .latitude(mapPin.getLatitude())
                .longitude(mapPin.getLongitude())
                .pinType(mapPin.getPinType())
                .createdAt(mapPin.getCreatedAt())
                .remainingDays(remainingDays)
                .tripId(mapPin.getTrip() != null ? mapPin.getTrip().getTripId() : null)
                .tripName(mapPin.getTrip() != null ? mapPin.getTrip().getTitle() : null)
                .startDate(mapPin.getTrip() != null ? mapPin.getTrip().getStartDate() : null)
                .endDate(mapPin.getTrip() != null ? mapPin.getTrip().getEndDate() : null);

        // ✅ title이 null이면 추가하지 않음
        if (mapPin.getTrip() != null && mapPin.getTrip().getTitle() != null) {
            dtoBuilder.title(mapPin.getTrip().getTitle());
        }

        if (mapPin.getPinType() == PinType.RECORDED) {
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
//    private MapPinResponseDto buildFavoredResponse(MapPin mapPin, String message, int remainingDays, Trip trip) {
//        return MapPinResponseDto.builder()
//                .pinId(mapPin.getPinId())
//                .googlePlaceId(mapPin.getGooglePlaceId())
//                .latitude(mapPin.getLatitude())
//                .longitude(mapPin.getLongitude())
//                .pinType(mapPin.getPinType())  // ✅ pinType 정상 반영
//                .createdAt(mapPin.getCreatedAt())
//                .remainingDays(remainingDays)
//
//                .tripId(mapPin.getTrip().getTripId())
//                .placeName(mapPin.getPlaceName())
//                .placeType(mapPin.getPlaceType()) // ✅ placeType 반환 (UNKNOWN 처리 적용)
//
//                .title(trip.getTitle())  // ✅ tripId를 통해 title 조회
//                .tripName(trip.getTitle())  // ✅ tripName 조회 (동일 값 사용)
//                .startDate(trip.getStartDate())  // ✅ startDate 조회
//                .endDate(trip.getEndDate())  // ✅ endDate 조회
//
//                .message(message)
//                .build();
//    }

    private MapPinResponseDto buildVisitResponse(MapPin mapPin, String message) {
        return MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .googlePlaceId(mapPin.getGooglePlaceId())
                .latitude(mapPin.getLatitude())
                .longitude(mapPin.getLongitude())
                .createdAt(mapPin.getCreatedAt())
                .placeName(mapPin.getPlaceName())
                .placeType(mapPin.getPlaceType() != null ? mapPin.getPlaceType() : PlaceType.UNKNOWN) // ✅ placeType 포함
                .message(message)
                .build();

    }

    private MapPinResponseDto buildRecordResponse(MapPin mapPin, String message) {
        return MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .pinType(mapPin.getPinType())
                .message(message)
                .build();
    }


    private MapPinResponseDto buildMapPinResponse(MapPin mapPin, String message, int remainingDays) {
        return MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .googlePlaceId(mapPin.getGooglePlaceId())
                .placeName(mapPin.getPlaceName())
                .placeType(mapPin.getPlaceType() != null ? mapPin.getPlaceType() : PlaceType.UNKNOWN) // ✅ placeType 포함
                .latitude(mapPin.getLatitude())
                .longitude(mapPin.getLongitude())
                .createdAt(mapPin.getCreatedAt())
                .remainingDays(remainingDays)       // ✅ 여행까지 남은 날짜 반환 추가
                .message(message)
                .build();
    }
}
