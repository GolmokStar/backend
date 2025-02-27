package com.golmok.golmokstar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Diary;
import com.golmok.golmokstar.entity.Trip;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.repository.DiaryRepository;
import com.golmok.golmokstar.repository.RecordRepository;
import com.golmok.golmokstar.repository.TripRepository;
import com.golmok.golmokstar.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final TripRepository tripRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    private static final String AI_DIARY_URL = "http://34.47.73.224:5000/diary";

    // 다이어리 작성 (생성)
    @Transactional
    public CreateDiaryResponseDto createDiary(CreateDiaryRequestDto dto) {
        Trip trip = tripRepository.findByTripId(dto.getTripId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 tripId를 찾을 수 없습니다."));

        // 일기 작성 날자가 Trip 의 startDate 와 endDate 사이에 있는지 검증
        if (dto.getDiaryDate().isBefore(trip.getStartDate()) && dto.getDiaryDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("다이어리 작성 날자는 여행 기간 내에 있어야 합니다");
        }

        // 빈 Diary 객체 생성한 다음 필드 세팅
        // 유효하지 않은 입력 데이터입니다 예외처리 해야함
        Diary diary = new Diary(
                trip,
                dto.getDiaryDate(),
                dto.getContent(),
                dto.getPhoto(),
                dto.getAiDraft()
        );

        diaryRepository.save(diary);

        return new CreateDiaryResponseDto(diary.getDiaryId());
    }

    // 다이어리 조회 (단일)
    @Transactional //(readOnly = true)
    public GetDiaryDetailResponseDto getDiaryDetail(Long diaryId) {
        Diary diary = diaryRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 diaryId를 찾을 수 없습니다."));

        return new GetDiaryDetailResponseDto(
                diary.getDiaryId(),
                diary.getTrip().getTripId(),
                diary.getDiaryDate(),
                diary.getContent(),
                diary.getPhoto(),
                diary.getAiDraft()
        );
    }

    // 특정 여행에 포함된 다이어리 모두 조회
    @Transactional
    public List<DiaryListResponseDto> getDiariesByTripId(Long tripId) {
        Trip trip = tripRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 tripId를 찾을 수 없습니다"));

        List<Diary> diaries = diaryRepository.findByTrip(trip);

        if (diaries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 tripId에 대한 다이어리를 찾을 수 없습니다.");
        }

        // diary 형태의 리스트 받아온 다음 DiaryListResponseDto 형태의 리스트로 바꿔서 반환
        return diaries.stream()
                .map(diary -> new DiaryListResponseDto(
                        diary.getDiaryId(),
                        trip.getTripId(),
                        diary.getDiaryDate(),
                        diary.getContent(),
                        diary.getPhoto(),
                        diary.getAiDraft()))
                .collect(Collectors.toList());
    }

    // 특정 월 방문 기록 & 일기 조회
    @Transactional
    public List<Map<String, Object>> getMonthlyDiaryHistoriesByMonth(int year, int month, Long userId) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // 유저 검증
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("userId 를 통해 사용자를 찾을 수 없습니다."));

        List<Map<String, Object>> records = recordRepository
                // 기간이 start ~ end 사이인, 특정 User 의 Record 객체들 조회 (MapPin -> User -> userId 순서로 연결)
                .findByVisitDateBetweenAndMapPin_User_UserId(start, end, userId)
                .stream()
                .map(record -> {
                    // 가변 객체를 담을 수 있는 HashMap 을 이용해 value 값을 Object 타입에 담는다.
                    Map<String, Object> map = new HashMap<>();
                    map.put("state", "history");
                    map.put("date", record.getVisitDate());
                    map.put("diaryId", null);
                    return map;
                })
                .toList();

        List<Map<String, Object>> diaries = diaryRepository
                // 기간이 start ~ end 사이인, 특정 User 의 Diary 객체들 조회 (Trip -> User -> userId 순서로 연결)
                .findByDiaryDateBetweenAndTrip_User_UserId(start, end, userId)
                .stream()
                .map(diary -> {
                    // 가변 객체를 담을 수 있는 HashMap 을 이용해 value 값을 Object 타입에 담는다.
                    Map<String, Object> map = new HashMap<>();
                    map.put("state", "diary");
                    map.put("date", diary.getDiaryDate());
                    map.put("diaryId", diary.getDiaryId());
                    return map;
                })
                .toList();

        // 두 리스트를 stream 으로 만든 뒤 병합하고 date
        List<Map<String, Object>> result = Stream.concat(records.stream(), diaries.stream())
                // Map 의 value 값은 Object 타입으로 업캐스팅되어 있으므로 Comparator 으로 비교할 땐 LocalDate 타입으로 강제 형변환해서 사용한다.
                // record 의 visitDate 와 diary 의 diaryDate 는 모두 nullable = false 이므로 null 처리는 따로 하지 않는다.
                .sorted(Comparator.comparing(map -> (LocalDate) map.get("date"), Comparator.reverseOrder()))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "선택한 월에 대한 방문 기록이 없습니다.");
        }

        return result;
    }

    // 날자 최신순 다이어리 조회 최대 5개
    @Transactional
    public List<GetRecentDiariesResponseDto> getRecentDiaries() {
        // 날자 최신순 다이어리 5개 조회하는 쿼리 날리고 리스트로 만들어서 저장
        List<GetRecentDiariesResponseDto> diaries = diaryRepository.findTop5ByOrderByDiaryDateDesc()
                .stream()
                // 반환용 dto 객체로 매핑
                .map(GetRecentDiariesResponseDto::fromEntityToDto)
                .toList();

        if(diaries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "작성된 다이어리가 없습니다.");
        }

        return diaries;
    }

    // 다이어리 삭제
    @Transactional
    public DeleteDiaryResponseDto deleteDiary(Long diaryId) {
        if(!diaryRepository.existsById(diaryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 requestId를 찾을 수 없습니다.");
        }

        diaryRepository.deleteById(diaryId);

        return new DeleteDiaryResponseDto(diaryId);
    }

    // ai 일기를 호출해 프론트에게 던져주기
    public AiDiaryResponseDto getAiDiary(LocalDate date, Long userId) {
        try {
            // 프론트에서 받아온 값으로 ai 일기를 호출하는 url을 설정한다.
            String requestUrl = String.format("%s?selected_date=%s&user_id=%d", AI_DIARY_URL, date, userId);
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(connection.getInputStream(), AiDiaryResponseDto.class);

        } catch (Exception e) {
            throw new RuntimeException("ai 일기 가져오기 실패..", e);
        }
    }
}
