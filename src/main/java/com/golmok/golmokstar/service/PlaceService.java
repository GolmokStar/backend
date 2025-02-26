package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.PlaceDetailResponseDto;
import com.golmok.golmokstar.dto.PlaceListResponseDto;
import com.golmok.golmokstar.dto.PlaceRequestDto;
import com.golmok.golmokstar.dto.PlaceResponseDto;
import com.golmok.golmokstar.entity.Place;
import com.golmok.golmokstar.exception.CustomException;
import com.golmok.golmokstar.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    // 새로운 장소 정보 등록
    @Transactional
    public PlaceResponseDto addPlace(PlaceRequestDto placeRequestDto) {
        // 중복 체크
        if (placeRepository.findByPlaceName(placeRequestDto.getPlaceName()).isPresent()) {
            throw new CustomException(400, "해당 장소가 이미 등록되어 있습니다.");
        }

        // 장소 저장
        Place place = Place.builder()
                .placeName(placeRequestDto.getPlaceName())
                .latitude(placeRequestDto.getLatitude())
                .longitude(placeRequestDto.getLongitude())
                .type(placeRequestDto.getType())
                .build();

        placeRepository.save(place);

        return PlaceResponseDto.builder()
                .placeId(place.getPlaceId())
                .message("장소가 성공적으로 등록되었습니다.")
                .build();
    }

    // 특정 장소의 상세 정보를 조회
    public PlaceDetailResponseDto getPlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomException(404, "해당 placeId에 대한 장소를 찾을 수 없습니다."));

        return PlaceDetailResponseDto.builder()
                .placeId(place.getPlaceId())
                .placeName(place.getPlaceName())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .type(place.getType())
                .build();
    }

    // 등록된 모든 장소의 목록을 조회
    // 등록된 배열이 없다면 [] 반환 (에러 X)
    public PlaceListResponseDto getAllPlaces() {
        List<PlaceDetailResponseDto> places = placeRepository.findAll().stream()
                .map(place -> PlaceDetailResponseDto.builder()
                        .placeId(place.getPlaceId())
                        .placeName(place.getPlaceName())
                        .latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .type(place.getType())
                        .build())
                .collect(Collectors.toList());

        return PlaceListResponseDto.builder().places(places).build();
    }

    // 특정 장소 삭제
    @Transactional
    public PlaceResponseDto deletePlace(Long placeId) {
        // 존재하는 장소인지 확인
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomException(404, "해당 placeId에 대한 장소를 찾을 수 없습니다."));

        placeRepository.delete(place);

        return PlaceResponseDto.builder()
                .placeId(placeId)
                .message("장소가 성공적으로 삭제되었습니다.")
                .build();
    }
}