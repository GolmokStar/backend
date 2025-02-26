package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Recommendation;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.repository.RecommendationRepository;
import com.golmok.golmokstar.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    // 추천 항목 추가
    @Transactional
    public CreateRecResponseDto createRecommendation(CreateRecRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 userId를 찾을 수 없습니다"));

        // Bad Request 핸들링 해야함

        Recommendation recommendation = new Recommendation();
        recommendation.setUser(user);
        recommendation.setType(dto.getType());
        recommendation.setSeason(dto.getSeason());
        recommendation.setAgeGroup(dto.getAgeGroup());
        recommendation.setRanking(dto.getRanking());

        Recommendation savedRecommendation = recommendationRepository.save(recommendation);

        return new CreateRecResponseDto(recommendation.getRecommendationId());
    }

    // 추천 항목 조회
    @Transactional
    public GetRecDetailResponseDto getRecDetailByRecommendationId(Long recommendationId) {
        Recommendation recommendation = recommendationRepository.findByRecommendationId(recommendationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 recommendationId를 찾을 수 없습니다."));

        return new GetRecDetailResponseDto(
                recommendation.getRecommendationId(),
                recommendation.getUser().getUserId(),
                recommendation.getType(),
                recommendation.getSeason(),
                recommendation.getAgeGroup(),
                recommendation.getRanking()
        );
    }

    // 추천 항목 목록 조회(사용자별)
    @Transactional
    public List<GetRecDetailListResponseDto> getRecsByUserId(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 userId를 찾을 수 없습니다"));

        List<Recommendation> recommendations = recommendationRepository.findByUser(user);

        // 컨트롤러에서 예외 핸들링 필요
        if(recommendations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 userId에 대한 추천 항목을 찾을 수 없습니다.");
        }

        // GetRecListResponseDto 객체의 리스트로 만들어 반환
        return recommendations.stream()
                .map(rec -> new GetRecDetailListResponseDto(
                        rec.getRecommendationId(),
                        rec.getType(),
                        rec.getSeason(),
                        rec.getAgeGroup(),
                        rec.getRanking()))
                .collect((Collectors.toList()));
    }

    // 추천 항목 삭제
    @Transactional
    public DeleteRecResponseDto deleteRecommendation(Long recommendationId) {
        if(!recommendationRepository.existsByRecommendationId(recommendationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 recommendationId를 찾을 수 없습니다.");
        }

        recommendationRepository.deleteById(recommendationId);

        return new DeleteRecResponseDto(recommendationId);
    }
}
