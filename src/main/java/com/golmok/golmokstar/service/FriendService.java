package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.*;
import com.golmok.golmokstar.entity.Friend;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.repository.FriendRepository;
import com.golmok.golmokstar.repository.InterestAreaRepository;
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
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final InterestAreaRepository interestAreaRepository;

    // 친구 목록 조회
    @Transactional
    public List<GetFriendListResponseDto> getFriendListByFriendCode (String friendCode) {
        // friendCode 로 user 객체를 찾는다.
        User user = userRepository.findByFriendCode(friendCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 friendCode 입니다."));

        // requester 혹은 receiver 가 해당 user 인 friend 객체들을 찾는다.
        List<Friend> friends = friendRepository.findByCurrentUserOrFriendUser(user, user);

        if (friends.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아직 친구가 없습니다. 친구 추가를 통해 친구를 만들어보세요!");
        }

        // 일부 정보는 friend 객체에서, 일부 정보는 user 객체에서 가져와야 하므로 user 객체로 매핑한다.
        return friends.stream()
                .map(friend -> {
                    // 해당 user 가 requester 일수도, receiver 일 수도 있으므로 이를 구분해서 user 객체로 매핑한다.
                    // 내가 아닌 친구의 정보를 가져와야 한다.
                    User friendUser = friend.getCurrentUser().equals(user) ? friend.getFriendUser() : friend.getCurrentUser();
                    return new GetFriendListResponseDto(
                            friend.getFriendId(),
                            friendUser.getProfilePhoto(),
                            friendUser.getNickname(),
                            friendUser.getFriendCode(),
                            friend.getTravelCountTogether(),
                            interestAreaRepository.findInterestsByUser(friendUser)
                    );
                })
                // map 의 반환값은 스트림이므로 이를 다시 리스트로 바꿔서 반환한다.
                .collect(Collectors.toList());
    }

    // 친구 삭제
    @Transactional
    public DeleteFriendResponseDto deleteFriend(Long friendid) {
        if(!friendRepository.existsByFriendId(friendid)) {
            throw new IllegalArgumentException("친구 관계가 없습니다.");
        }

        friendRepository.deleteById(friendid);

        return new DeleteFriendResponseDto(friendid);
    }
}