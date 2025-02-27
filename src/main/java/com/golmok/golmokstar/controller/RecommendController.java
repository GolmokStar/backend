package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.service.RecommendService;
import com.golmok.golmokstar.service.RecommendService.RecommendationItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping
    public ResponseEntity<List<RecommendationItem>> getRecommendations(
            @RequestHeader("Authorization") String token) {

        String accessToken = token.replace("Bearer ", ""); // Bearer 제거
        return recommendService.getRecommendations(accessToken);
    }
}
