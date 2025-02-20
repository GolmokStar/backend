package com.golmok.golmokstar.service;

import com.golmok.golmokstar.dto.AddFriendRequestDto;
import com.golmok.golmokstar.dto.UpdateFriendRequestDto;
import com.golmok.golmokstar.dto.AddFriendResponseDto;
import com.golmok.golmokstar.dto.DeleteFriendResponseDto;
import com.golmok.golmokstar.dto.GetFriendDetailResponseDto;
import com.golmok.golmokstar.dto.UpdateFriendResponseDto;
import com.golmok.golmokstar.entity.Friend;
import com.golmok.golmokstar.entity.User;
import com.golmok.golmokstar.repository.FriendRepository;
import com.golmok.golmokstar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
//@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;

    }

    @Transactional
    public AddFriendResponseDto addFriend(AddFriendRequestDto dto) {
        User currentUser = userRepository.findById(dto.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자ID 입니다."));
        User friendUser = userRepository.findById(dto.getFriendUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 친구ID 입니다."));

        if(friendRepository.existsByCurrentUserAndFriendUser(currentUser, friendUser)) {
            throw new IllegalArgumentException("이미 친구 관계가 존재합니다");
        }

        Friend newFriend = new Friend(currentUser, friendUser, LocalDateTime.now(), 0);
        friendRepository.save(newFriend);

        return new AddFriendResponseDto(newFriend.getFriendId(), newFriend.getFriendshipConfirmedDate());
    }

    @Transactional
    public GetFriendDetailResponseDto getFriendDetail(Long friendId) {
        Friend friend = friendRepository.findByFriendId(friendId)
                .orElseThrow(() -> new IllegalArgumentException(("해당 friendId를 찾을 수 없습니다")));

        return new GetFriendDetailResponseDto(
                friend.getFriendId(),
                friend.getCurrentUser().getUserId(),
                friend.getFriendUser().getUserId(),
                friend.getFriendshipConfirmedDate(),
                friend.getTravelCount()
        );
    }

    @Transactional
    public UpdateFriendResponseDto updateFriend(Long friendId, UpdateFriendRequestDto dto) {
        if(dto.getTravelCount() == null || dto.getTravelCount() <= 0) {
            throw new IllegalArgumentException("유효하지 않은 입력 데이터입니다.");
        }

        Friend friend = friendRepository.findByFriendId(friendId)
                .orElseThrow(() -> new IllegalArgumentException("해당 friendId를 찾을 수 없습니다"));

        friend.setTravelCount(dto.getTravelCount());
        friendRepository.save(friend);

        return new UpdateFriendResponseDto(friend.getFriendId());
    }

    @Transactional
    public DeleteFriendResponseDto deleteFriend(Long friendId) {
        if(!friendRepository.existsById(friendId)) {
            throw new IllegalArgumentException("해당 friendId를 찾을 수 없습니다.");
        }

        friendRepository.deleteById(friendId);

        return new DeleteFriendResponseDto(friendId);
    }

}