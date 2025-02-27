package com.golmok.golmokstar.service;

import com.golmok.golmokstar.config.JwtUtil;
import com.golmok.golmokstar.dto.MapPinRecordRequestDto;
import com.golmok.golmokstar.dto.MapPinRequestDto;
import com.golmok.golmokstar.dto.MapPinResponseDto;
import com.golmok.golmokstar.dto.MapPinVisitRequestDto;
import com.golmok.golmokstar.entity.MapPin;
import com.golmok.golmokstar.entity.Record;
import com.golmok.golmokstar.entity.Place;
import com.golmok.golmokstar.entity.Trip;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.enums.PinType;
import com.golmok.golmokstar.enums.PlaceType;
import com.golmok.golmokstar.exception.CustomException;
import com.golmok.golmokstar.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final PlaceRepository placeRepository;
    private final RecordRepository recordRepository;
    private final JwtUtil jwtUtil; //JWT에서 userId를 추출하는 유틸리티 추가

    // 장소 찜하기
    @Transactional
    public MapPinResponseDto addFavoredMapPin(String token, MapPinRequestDto mapPinRequestDto) {

        //"Bearer " 접두사 제거 후 JWT에서 userId 추출
        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken); // 🔹 JWT에서 userId 추출

        // 여행 존재 여부 확인
        Trip trip = tripRepository.findById(mapPinRequestDto.getTripId())
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다"));

        // ✅ 사용자는 accessToken으로 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(401, "해당 userId를 찾을 수 없습니다."));

        // ❌ Place 참조하지 않도록
//        // 장소 존재 여부 확인
//        Place place = placeRepository.findByGooglePlaceId(mapPinRequestDto.getGooglePlaceId())
//                .orElseGet(() -> {
//                    Place newPlace = Place.builder()
//                            .googlePlaceId(mapPinRequestDto.getGooglePlaceId())
//                            .placeName(mapPinRequestDto.getPlaceName())
//                            .latitude(00.00)    // 임시 좌표
//                            .longitude(00.00)   // 임시 좌표
//                            .build();
//                    return placeRepository.save(newPlace);
//                });

        // 🔹 MapPin 저장
        MapPin mapPin = MapPin.builder()
                .trip(trip)
                .user(user)
                .googlePlaceId(mapPinRequestDto.getGooglePlaceId())     // ✅ place 엔티티 사용 X
                .placeName(mapPinRequestDto.getPlaceName())     // ✅ place 엔티티 사용 X
                .placeType(mapPinRequestDto.getPlaceType())    // enum -> string 변환
                .latitude(mapPinRequestDto.getLatitude())
                .longitude(mapPinRequestDto.getLongitude())
                .pinType(PinType.FAVORED)
                .createdAt(LocalDateTime.now()) // 찜하기 성공한 시간을 반환
                .build();

        mapPinRepository.save(mapPin);

        // 여행 종료일까지 남은 일수 계산
        int remainingDays = (int) ChronoUnit.DAYS.between(LocalDateTime.now(), trip.getStartDate());

        return MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .googlePlaceId(mapPin.getGooglePlaceId())
                .latitude(mapPinRequestDto.getLatitude())
                .longitude(mapPinRequestDto.getLongitude())
                .remainingDays(remainingDays)
                .createdAt(LocalDate.now())
                .message("장소 찜 완료")
                .build();
    }

    // 장소 방문하기 (VISITED_PENDING)
    @Transactional
    public MapPinResponseDto addVisitedMapPin(String token, MapPinVisitRequestDto mapPinVisitRequestDto) {

        //"Bearer " 접두사 제거 후 JWT에서 userId 추출
        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken); // 🔹 JWT에서 userId 추출

        // ✅ 사용자는 accessToken으로 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(401, "해당 userId를 찾을 수 없습니다."));

        // 여행 존재 여부 확인
        Trip trip = tripRepository.findById(mapPinVisitRequestDto.getTripId())
                .orElseThrow(() -> new CustomException(404, "해당 tripId를 찾을 수 없습니다."));

        // 위치 검증
        if (!isWithinRange(mapPinVisitRequestDto.getLatitude(), mapPinVisitRequestDto.getLongitude(),
                mapPinVisitRequestDto.getDeviceLatitude(), mapPinVisitRequestDto.getDeviceLongitude())) {
            throw new CustomException(400, "현재 위치가 장소에서 500m 이상 떨어져 있어서 방문을 저장할 수 없습니다.");
        }

        // mapPin 저장
        MapPin mapPin = MapPin.builder()
                .trip(trip)
                .user(user)
                .googlePlaceId(mapPinVisitRequestDto.getGooglePlaceId())
                .latitude(mapPinVisitRequestDto.getLatitude())
                .longitude(mapPinVisitRequestDto.getLongitude())
                .pinType(PinType.VISITED_PENDING)
                .build();

        mapPinRepository.save(mapPin);

        return MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .googlePlaceId(mapPin.getGooglePlaceId())
                .latitude(mapPin.getLatitude())
                .longitude(mapPin.getLongitude())
                .createdAt(LocalDate.now())     // 방문하기 성공 시간 반환
                .build();
    }

    // 장소 기록하기 상태로 변경
    @Transactional
    public MapPinResponseDto updateRecordStatus(String token, MapPinRecordRequestDto mapPinRecordRequestDto) {

        //"Bearer " 접두사 제거 후 JWT에서 userId 추출
        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken); // 🔹 JWT에서 userId 추출

        // pinId 존재 여부 확인
        MapPin mapPin = mapPinRepository.findById(mapPinRecordRequestDto.getPinId())
                .orElseThrow(() -> new CustomException(404, "해당 pinId를 찾을 수 없습니다."));

        // 사용자가 pinId를 소유하고 있는지 검증
        if(!mapPin.getUser().getUserId().equals(userId)) {
            throw new CustomException(403, "이 장소의 기록을 변경할 권한이 없습니다.");
        }

        // pinType을 RECORDED로 변경
        mapPin.setPinType(PinType.RECORDED);
        mapPinRepository.save(mapPin);

        return MapPinResponseDto.builder()
                .pinId(mapPin.getPinId())
                .message("장소의 상태가 기록 상태로 변경되었습니다.")
                .build();
    }

    // 장소 조회(전체)
    @Transactional(readOnly = true)
    public List<MapPinResponseDto> getAllMapPins(String token) {

        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken);

        // 사용자의 모든 mapPin 조회
        // ✅ findByUserId -> findByUser_UserId 수정
        List<MapPin> mapPins = mapPinRepository.findByUser_UserId(userId);

        // MapPin을 DTO로 변환하여 반환
        return mapPins.stream().map(mapPin -> {
            MapPinResponseDto.MapPinResponseDtoBuilder dtoBuilder = MapPinResponseDto.builder()
                    .pinId(mapPin.getPinId())
                    .googlePlaceId(mapPin.getGooglePlaceId())
                    .placeName(mapPin.getPlaceName())
                    .latitude(mapPin.getLatitude())
                    .longitude(mapPin.getLongitude())
                    .createdAt(LocalDate.now());

            // record 상태일 경우 추가
            if(mapPin.getPinType() == PinType.RECORDED && mapPin.getTrip() != null) {
                dtoBuilder.tripName(mapPin.getTrip().getTitle())
                        .startDate(mapPin.getTrip().getStartDate())
                        .endDate(mapPin.getTrip().getEndDate());

                // rating을 Record에서 가져오기
                Optional<Record> record = recordRepository.findByMapPin(mapPin);
                record.ifPresent(r -> dtoBuilder.rating(r.getRating()));
            }

            return dtoBuilder.build();
        }).collect(Collectors.toList());
    }

    // 특정 여행(tripId)과 연결된 장소 조회
    @Transactional(readOnly = true)
    public List<MapPinResponseDto> getMapPinsByTripId(String token, Long tripId) {
        String accessToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(accessToken);

        // tripId로 장소 조회
        // ✅ findByUserId -> findByUser_UserId 수정
        List<MapPin> mapPins = mapPinRepository.findByUser_UserId(tripId);

        if(mapPins.isEmpty()) {
            throw new CustomException(404, "해당 tripId에 대한 장소를 찾을 수 없습니다.");
        }

        // MapPin을 DTO로 변환하여 반환
        return mapPins.stream().map(mapPin -> {
            MapPinResponseDto.MapPinResponseDtoBuilder dtoBuilder = MapPinResponseDto.builder()
                    .pinId(mapPin.getPinId())
                    .googlePlaceId(mapPin.getGooglePlaceId())
                    .placeName(mapPin.getPlaceName())
                    .latitude(mapPin.getLatitude())
                    .longitude(mapPin.getLongitude())
                    .pinType(mapPin.getPinType())
                    .tripId(mapPin.getTrip().getTripId())
                    .tripName(mapPin.getTrip().getTitle())
                    .createdAt(LocalDate.now());

            // RECORDED 상태라면 기록(`Record`)에서 별점 가져오기
            if (mapPin.getPinType() == PinType.RECORDED) {
                Optional<Record> record = recordRepository.findByMapPin(mapPin);
                record.ifPresent(r -> dtoBuilder.rating(r.getRating())); // rating을 Record에서 가져오기
            }

            return dtoBuilder.build();
        }).collect(Collectors.toList());
    }

    // ✅ 두 좌표가 500m 이내인지 계산
    private boolean isWithinRange(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;      // 지구 반지름 (m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLon/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLat/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;    // 거리 (m)
        return distance <= 500;     // 500m 이내인지 확인
    }
    // ❌ 메서드 변경으로 삭제
//    // 장소 방문 처리 (값: FAVORED, VISITED_PENDING, RECORDED)
//    @Transactional
//    public MapPinResponseDto visitPlace(MapPinVisitRequestDto request) {
//        // MapPin 존재 여부 확인
//        MapPin mapPin = mapPinRepository.findById(request.getPinId())
//                .orElseThrow(() -> new CustomException(404, "해당 pinId를 찾을 수 없습니다."));
//
//        // pinType 값 검증 (FAVORED, VISITED_PENDING, RECORDED만 허용)
//        if(request.getPinType() != PinType.FAVORED
//                && request.getPinType() != PinType.VISITED_PENDING
//                && request.getPinType() != PinType.RECORDED) { // ✅ 모든 경우 검증
//            throw new CustomException(400, "올바른 pinType이 아닙니다.");
//        }
//
//        // pinType 변경
//        mapPin.setPinType(request.getPinType());
//        mapPinRepository.save(mapPin);
//
//        return MapPinResponseDto.builder()
//                .pinId(mapPin.getPinId()) // ✅ Long 타입 유지
//                .message("장소 방문하기 성공")
//                .build();
//    }
}