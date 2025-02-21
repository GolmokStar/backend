package com.golmok.golmokstar.controller;

import com.golmok.golmokstar.entity.InterestArea;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.repository.InterestAreaRepository;
import com.golmok.golmokstar.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final InterestAreaRepository interestAreaRepository;

    /**
     * 프로필 수정 (닉네임、 프로필 사진、관심 분야만 변경 가능)
     */
    @PatchMapping("/me")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @RequestBody Map<String, Object> profileData) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
        }

        User user = userOptional.get();

        //닉네임 변경 - 중복 가능
        if (profileData.containsKey("nickname")) {
            String newNickname = (String) profileData.get("nickname");
            user.setNickname(newNickname);
        }

        //프로필 사진 수정
        String profilePhoto = (String) profileData.get("profilePhoto");
        if (profilePhoto != null) {
            user.setProfilePhoto(profilePhoto);
        }

        userRepository.save(user);

        //관심 분야 업데이트
        List<String> interestAreas = (List<String>) profileData.get("interestAreas");
        if (interestAreas != null) {
            interestAreaRepository.deleteAll(interestAreaRepository.findByUser(user));
            for (String interest : interestAreas) {
                InterestArea newInterest = new InterestArea();
                newInterest.setUser(user);
                newInterest.setInterest(interest);
                interestAreaRepository.save(newInterest);
            }
        }

        return ResponseEntity.ok(Map.of("message", "프로필이 성공적으로 업데이트되었습니다."));
    }


    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
        }

        User user = userOptional.get();

        //추후 생성되는 테이블도 추가해야 함.
        //관심 분야 삭제
        interestAreaRepository.deleteAll(interestAreaRepository.findByUser(user));

        //유저 삭제
        userRepository.delete(user);

        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }

    //회원 정보 조회 (GET /users/me)
    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다."));
        }
        User user = userOptional.get();
        List<String> interestAreas = interestAreaRepository.findByUser(user).stream()
                .map(InterestArea::getInterest)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "userId", user.getUserId(),
                "nickname", user.getNickname(),
                "gender", user.getGender(),
                "birthDate", user.getBirthDate(),
                "profilePhoto", user.getProfilePhoto(),
                "travelCount", user.getRecordCount(),
                "recordCount", 0,
                "interestAreas", interestAreas
        ));
    }
}